package se.sundsvall.vacationdocument.integration.opene;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;

@ExtendWith({ResourceLoaderExtension.class, MockitoExtension.class})
class OpenEIntegrationTests {

    private byte[] documentsXml;
    private byte[] documentXml_206830;
    private byte[] documentXml_206832;
    private byte[] documentXml_206833;

    @Mock
    private OpenEClientProperties mockProperties;

    @Mock
    private OpenEClient mockClient;

    @InjectMocks
    private OpenEIntegration openEIntegration;

    @BeforeEach
    void setUp(@Load("/open-e/open-e-documents.xml") final String documentsXml,
            @Load("/open-e/open-e-document-206830.xml") final String documentXml_206830,
            @Load("/open-e/open-e-document-206832.xml") final String documentXml_206832,
            @Load("/open-e/open-e-document-206833.xml") final String documentXml_206833) {
        this.documentsXml = documentsXml.getBytes(UTF_8);
        this.documentXml_206830 = documentXml_206830.getBytes(UTF_8);
        this.documentXml_206832 = documentXml_206832.getBytes(UTF_8);
        this.documentXml_206833 = documentXml_206833.getBytes(UTF_8);
    }

    @Test
    void testGetDocuments() {
        var familyId = "999";
        var fromDate = "2024-05-01";
        var toDate = "2024-06-16";

        when(mockProperties.familyId()).thenReturn(familyId);
        when(mockProperties.approvedByManagerStatusId()).thenReturn("3618");

        when(mockClient.getErrands(familyId, fromDate, toDate)).thenReturn(documentsXml);
        when(mockClient.getErrand("206830")).thenReturn(documentXml_206830);
        when(mockClient.getErrand("206832")).thenReturn(documentXml_206832);
        when(mockClient.getErrand("206833")).thenReturn(documentXml_206833);

        var documents = openEIntegration.getDocuments(fromDate, toDate);

        assertThat(documents).hasSize(3);
        assertThat(documents.stream().filter(OpenEDocument::approvedByManager)).hasSize(2);
        assertThat(documents.stream().filter(not(OpenEDocument::approvedByManager))).hasSize(1);

        verify(mockProperties).familyId();
        verify(mockProperties, times(3)).approvedByManagerStatusId();
        verifyNoMoreInteractions(mockProperties);
        verify(mockClient).getErrands(familyId, fromDate, toDate);
        verify(mockClient, times(3)).getErrand(anyString());
        verifyNoMoreInteractions(mockClient);
    }
}
