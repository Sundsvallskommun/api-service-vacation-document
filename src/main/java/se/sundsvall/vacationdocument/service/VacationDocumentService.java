package se.sundsvall.vacationdocument.service;

import static generated.se.sundsvall.party.PartyType.PRIVATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.mapToDocumentCreateRequest;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.mapToRenderRequestParameters;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private final TemplatingClient templatingClient;
    private final DocumentClient documentClient;
    private final PartyClient partyClient;

    VacationDocumentService(final OpenEIntegration openEIntegration,
            final TemplatingClient templatingClient, final DocumentClient documentClient,
            final PartyClient partyClient) {
        this.openEIntegration = openEIntegration;
        this.templatingClient = templatingClient;
        this.documentClient = documentClient;
        this.partyClient = partyClient;
    }

    void processDocuments(final LocalDate fromDate, final LocalDate toDate) {
        // Get OpenE documents
        var documents = openEIntegration.getDocuments(fromDate.format(ISO_LOCAL_DATE), toDate.format(ISO_LOCAL_DATE));

        for (var document : documents) {
            try {
                var renderRequest = new RenderRequest()
                    .identifier("someIdentifier")
                    .parameters(mapToRenderRequestParameters(document));
                // Render the OpenE document as a PDF
                var renderResponse = templatingClient.renderPdf(renderRequest);
                var output = renderResponse.getOutput();
                var pdfData = Base64.getDecoder().decode(output);

                // Translate the document SSN to a party-id
                var partyId = partyClient.getPartyId(PRIVATE, document.employeeInformation().ssn());

                if (partyId.isEmpty()) {
                    LOG.warn("Unable to get party id for SSN {}", document.employeeInformation().ssn());

                    continue;
                }

                // Create the document
                var documentCreateRequest = mapToDocumentCreateRequest(partyId.get(), document);
                var filename = "Semesterväxlingsdokument_%s.pdf".formatted(partyId.get());
                var documentMultipartFile = new DocumentMultipartFile(filename, pdfData);

                // Create the document
                documentClient.createDocument(documentCreateRequest, List.of(documentMultipartFile));

                LOG.info("Created document {}", filename);
            } catch (Exception e) {
                LOG.warn("Unable to create document", e);
            }
        }
    }
}
