package se.sundsvall.vacationdocument.service;

import static generated.se.sundsvall.party.PartyType.PRIVATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static se.sundsvall.vacationdocument.model.DocumentStatus.DONE;
import static se.sundsvall.vacationdocument.model.DocumentStatus.FAILED;
import static se.sundsvall.vacationdocument.model.DocumentStatus.NOT_APPROVED;
import static se.sundsvall.vacationdocument.model.DocumentStatus.PROCESSING;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.mapToDocumentCreateRequest;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.mapToRenderRequestParameters;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.sundsvall.vacationdocument.integration.db.DbIntegration;
import se.sundsvall.vacationdocument.integration.document.DocumentClient;
import se.sundsvall.vacationdocument.integration.document.DocumentMultipartFile;
import se.sundsvall.vacationdocument.integration.opene.OpenEIntegration;
import se.sundsvall.vacationdocument.integration.party.PartyClient;
import se.sundsvall.vacationdocument.integration.templating.TemplatingClient;

import generated.se.sundsvall.templating.RenderRequest;

@Service
class VacationDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(VacationDocumentService.class);

    private final OpenEIntegration openEIntegration;
    private final DbIntegration dbIntegration;
    private final TemplatingClient templatingClient;
    private final DocumentClient documentClient;
    private final PartyClient partyClient;

    VacationDocumentService(final OpenEIntegration openEIntegration,
            final DbIntegration dbIntegration, final TemplatingClient templatingClient,
            final DocumentClient documentClient, final PartyClient partyClient) {
        this.openEIntegration = openEIntegration;
        this.dbIntegration = dbIntegration;
        this.templatingClient = templatingClient;
        this.documentClient = documentClient;
        this.partyClient = partyClient;
    }

    void processDocuments(final String municipalityId, final LocalDate fromDate, final LocalDate toDate) {
        // Get OpenE documents in the given date range
        var openEDocuments = openEIntegration.getDocuments(fromDate.format(ISO_LOCAL_DATE), toDate.format(ISO_LOCAL_DATE));

        for (var openEDocument : openEDocuments) {
            var documentId = openEDocument.id();

            // Skip the OpenE document if it's already processed
            if (dbIntegration.existsById(municipalityId, documentId)) {
                LOG.info("Skipping previously processed document {} (municipalityId: {})", documentId, municipalityId);

                continue;
            }

            // Save the initial local document
            dbIntegration.saveDocument(documentId, municipalityId, openEDocument.approvedByManager() ? PROCESSING : NOT_APPROVED);

            // Bail out if the OpenE document isn't approved by manager
            if (!openEDocument.approvedByManager()) {
                LOG.info("Document {} is NOT approved by manager. Skipping further processing (municipalityId: {})", documentId, municipalityId);

                continue;
            }

            try {
                var renderRequest = new RenderRequest()
                    .identifier("someIdentifier")
                    .parameters(mapToRenderRequestParameters(openEDocument));
                // Render the OpenE document as a PDF
                var renderResponse = templatingClient.renderPdf(municipalityId, renderRequest);
                var output = renderResponse.getOutput();
                var pdfData = Base64.getDecoder().decode(output);

                // Translate the document SSN to a party-id
                var partyId = partyClient.getPartyId(municipalityId, PRIVATE, openEDocument.employeeInformation().ssn());

                if (partyId.isEmpty()) {
                    LOG.warn("Unable to get party id for SSN {} (municipalityId: {})", openEDocument.employeeInformation().ssn(), municipalityId);

                    // Update the local document
                    dbIntegration.updateDocument(documentId, municipalityId, FAILED, "Unable to get party id");

                    continue;
                }

                // Create the document
                var documentCreateRequest = mapToDocumentCreateRequest(partyId.get(), openEDocument);
                var filename = "Semesterväxlingsdokument_%s.pdf".formatted(partyId.get());
                var documentMultipartFile = new DocumentMultipartFile(filename, pdfData);

                // Create the document
                documentClient.createDocument(municipalityId, documentCreateRequest, List.of(documentMultipartFile));

                LOG.info("Created document {} - {} (municipalityId: {})", documentId, filename, municipalityId);

                // Update the local document
                dbIntegration.updateDocument(documentId, municipalityId, DONE);
            } catch (Exception e) {
                LOG.warn("Unable to create document {} (municipalityId: {})", openEDocument.id(), municipalityId, e);

                // Update the local document
                dbIntegration.updateDocument(documentId, municipalityId, FAILED, e.getMessage());
            }
        }
    }
}
