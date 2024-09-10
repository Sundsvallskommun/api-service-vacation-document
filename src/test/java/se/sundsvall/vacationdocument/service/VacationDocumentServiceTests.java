package se.sundsvall.vacationdocument.service;

import static generated.se.sundsvall.party.PartyType.PRIVATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_SSN;
import static se.sundsvall.vacationdocument.TestDataFactory.OUTPUT;
import static se.sundsvall.vacationdocument.TestDataFactory.createOpenEDocument;
import static se.sundsvall.vacationdocument.model.DocumentStatus.DONE;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.EMAIL_ADDRESS;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.FIRST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.JOB_TITLE;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.LAST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.MOBILE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.ORGANIZATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.PARTY_ID;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.PHONE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.USERNAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.employeeKey;
import static se.sundsvall.vacationdocument.service.VacationDocumentMapper.managerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.vacationdocument.integration.db.DbIntegration;
import se.sundsvall.vacationdocument.integration.document.DocumentClient;
import se.sundsvall.vacationdocument.integration.document.DocumentMultipartFile;
import se.sundsvall.vacationdocument.integration.opene.OpenEIntegration;
import se.sundsvall.vacationdocument.integration.party.PartyClient;
import se.sundsvall.vacationdocument.integration.templating.TemplatingClient;
import se.sundsvall.vacationdocument.model.DocumentStatus;

import generated.se.sundsvall.document.Confidentiality;
import generated.se.sundsvall.document.DocumentCreateRequest;
import generated.se.sundsvall.document.DocumentMetadata;
import generated.se.sundsvall.templating.RenderRequest;
import generated.se.sundsvall.templating.RenderResponse;

@ExtendWith({ ResourceLoaderExtension.class, MockitoExtension.class })
class VacationDocumentServiceTests {

    @Mock
    private OpenEIntegration openEIntegrationMock;
    @Mock
    private DbIntegration dbIntegrationMock;
    @Mock
    private TemplatingClient templatingClientMock;
    @Mock
    private DocumentClient documentClientMock;
    @Mock
    private PartyClient partyClientMock;

    @Captor
    private ArgumentCaptor<DocumentCreateRequest> documentRequestCaptor;
    @Captor
    private ArgumentCaptor<List<DocumentMultipartFile>> documentFilesCaptor;

    @InjectMocks
    private VacationDocumentService service;

    @Test
    void testProcessDocuments() {
        var partyId = "somePartyId";
        var from = LocalDate.of(2024, 1, 1);
        var to = LocalDate.of(2024, 1, 31);
        var doc1 = createOpenEDocument("someId", "someName", true);
        var doc2 = createOpenEDocument("someOtherId", "someOtherName", false);

        when(openEIntegrationMock.getDocuments(from.format(ISO_LOCAL_DATE), to.format(ISO_LOCAL_DATE)))
            .thenReturn(List.of(doc1, doc2));
        when(templatingClientMock.renderPdf(any(RenderRequest.class)))
            .thenReturn(new RenderResponse().output(OUTPUT));
        when(partyClientMock.getPartyId(PRIVATE, EMPLOYEE_SSN))
            .thenReturn(Optional.of(partyId))
            .thenReturn(Optional.empty());
        when(documentClientMock.createDocument(any(DocumentCreateRequest.class), anyList()))
            .thenReturn(new ResponseEntity<>(CREATED));

        service.processDocuments(from, to);

        verify(openEIntegrationMock).getDocuments(from.format(ISO_LOCAL_DATE), to.format(ISO_LOCAL_DATE));
        verify(dbIntegrationMock, times(2)).existsById(anyString());
        verify(dbIntegrationMock, times(2)).saveDocument(anyString(), any(DocumentStatus.class));
        verify(dbIntegrationMock, never()).updateDocument(eq(doc2.id()), any(DocumentStatus.class));
        verify(dbIntegrationMock).updateDocument(doc1.id(), DONE);
        verify(templatingClientMock).renderPdf(any(RenderRequest.class));
        verify(partyClientMock).getPartyId(PRIVATE, EMPLOYEE_SSN);
        verify(documentClientMock).createDocument(documentRequestCaptor.capture(), documentFilesCaptor.capture());
        verifyNoMoreInteractions(openEIntegrationMock, dbIntegrationMock, templatingClientMock, partyClientMock, documentClientMock);

        assertThat(documentRequestCaptor.getValue()).satisfies(request -> {
            assertThat(request.getDescription()).isEqualTo(doc1.name());
            assertThat(request.getConfidentiality()).matches(not(Confidentiality::getConfidential));
            assertThat(request.getArchive()).isFalse();
            assertThat(request.getCreatedBy()).isEqualTo(doc1.employeeInformation().firstName() + " " + doc1.employeeInformation().lastName());
            assertThat(request.getMetadataList()).extracting(DocumentMetadata::getKey).containsExactlyInAnyOrder(
                employeeKey(PARTY_ID),
                employeeKey(FIRST_NAME),
                employeeKey(LAST_NAME),
                employeeKey(EMAIL_ADDRESS),
                employeeKey(PHONE_NUMBER),
                employeeKey(MOBILE_NUMBER),
                employeeKey(JOB_TITLE),
                employeeKey(ORGANIZATION),
                managerKey(FIRST_NAME),
                managerKey(LAST_NAME),
                managerKey(USERNAME),
                managerKey(JOB_TITLE),
                managerKey(ORGANIZATION));
            assertThat(request.getMetadataList()).extracting(DocumentMetadata::getValue).containsExactlyInAnyOrder(
                partyId,
                doc1.employeeInformation().firstName(),
                doc1.employeeInformation().lastName(),
                doc1.employeeInformation().emailAddress(),
                doc1.employeeInformation().phoneNumber(),
                doc1.employeeInformation().mobileNumber(),
                doc1.employeeInformation().jobTitle(),
                doc1.employeeInformation().organization(),
                doc1.managerInformation().firstName(),
                doc1.managerInformation().lastName(),
                doc1.managerInformation().username(),
                doc1.managerInformation().jobTitle(),
                doc1.managerInformation().organization());
        });
        assertThat(documentFilesCaptor.getValue()).hasSize(1).first().satisfies(documentFile ->
            assertThat(documentFile.getOriginalFilename()).isEqualTo("Semesterväxlingsdokument_%s.pdf".formatted(partyId)));
    }

    @Test
    void testProcessDocumentsWhenLocalDocumentAlreadyExists() {
        var from = LocalDate.of(2024, 1, 1);
        var to = LocalDate.of(2024, 1, 31);
        var doc = createOpenEDocument("someId", "someName", true);

        when(openEIntegrationMock.getDocuments(from.format(ISO_LOCAL_DATE), to.format(ISO_LOCAL_DATE)))
            .thenReturn(List.of(doc));
        when(dbIntegrationMock.existsById(doc.id())).thenReturn(true);

        service.processDocuments(from, to);

        verify(openEIntegrationMock).getDocuments(from.format(ISO_LOCAL_DATE), to.format(ISO_LOCAL_DATE));
        verify(dbIntegrationMock).existsById(anyString());
        verifyNoMoreInteractions(openEIntegrationMock, dbIntegrationMock, templatingClientMock, partyClientMock, documentClientMock);
    }
}
