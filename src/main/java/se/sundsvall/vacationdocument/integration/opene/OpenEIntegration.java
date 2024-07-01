package se.sundsvall.vacationdocument.integration.opene;

import static se.sundsvall.vacationdocument.integration.opene.EmployeeInformationBuilder.newEmployeeInformation;
import static se.sundsvall.vacationdocument.integration.opene.ManagerInformationBuilder.newManagerInformation;
import static se.sundsvall.vacationdocument.integration.opene.OpenEDocumentBuilder.newDocument;
import static se.sundsvall.vacationdocument.integration.opene.util.XPathUtil.evaluateXPath;
import static se.sundsvall.vacationdocument.integration.opene.util.XPathUtil.getString;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class OpenEIntegration {

    private static final String DOCUMENTS_ID_PATH = "/FlowInstances/FlowInstance/flowInstanceID";
    private static final String DOCUMENT_ID_PATH = "/FlowInstance/Header/FlowInstanceID";
    private static final String DESCRIPTION_PATH = "/FlowInstance/Header/Flow/Name";
    private static final String STATUS_ID_PATH = "/FlowInstance/Header/Status/ID";
    private static final String EMPLOYEE_INFO_PATH = "/FlowInstance/Values/Medarbetaruppgifter";
    private static final String MANAGER_INFO_PATH = "/FlowInstance/Values/Manager";

    private final OpenEClientProperties properties;
    private final OpenEClient client;

    OpenEIntegration(final OpenEClientProperties properties, final OpenEClient client) {
        this.properties = properties;
        this.client = client;
    }

    public List<OpenEDocument> getDocuments(final String fromDate, final String toDate) {
        return getDocumentIds(fromDate, toDate).stream()
            // Get the errand
            .map(client::getErrand)
            // Map the XML to an OpenE document
            .map(this::mapToDocument)
            .toList();
    }

    List<String> getDocumentIds(final String fromDate, final String toDate) {
        // Get the document list from OpenE
        var documentsXml = client.getErrands(properties.familyId(), fromDate, toDate);
        // Extract the document id:s
        var result = evaluateXPath(documentsXml, DOCUMENTS_ID_PATH);
        // Trim the errand id:s, just to be safe(r)
        return result.eachText().stream()
            .map(String::trim)
            .toList();
    }

    OpenEDocument mapToDocument(final byte[] documentXml) {
        var statusId = getString(documentXml, STATUS_ID_PATH);

        return newDocument()
            .withId(getString(documentXml, DOCUMENT_ID_PATH))
            .withName(getString(documentXml, DESCRIPTION_PATH))
            .withApprovedByManager(properties.approvedByManagerStatusId().equals(statusId))
            .withEmployeeInformation(newEmployeeInformation()
                .withFirstName(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/firstname")))
                .withLastName(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/lastname")))
                .withEmailAddress(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/email")))
                .withPhoneNumber(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/phone")))
                .withMobileNumber(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/mobilePhone")))
                .withSsn(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/citizenIdentifier")))
                .withJobTitle(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/title")))
                .withOrganization(getString(documentXml, EMPLOYEE_INFO_PATH.concat("/organization")))
                .build())
            .withManagerInformation(newManagerInformation()
                .withUsername(getString(documentXml, MANAGER_INFO_PATH.concat("/username")))
                .withFirstName(getString(documentXml, MANAGER_INFO_PATH.concat("/firstname")))
                .withLastName(getString(documentXml, MANAGER_INFO_PATH.concat("/lastname")))
                .withJobTitle(getString(documentXml, MANAGER_INFO_PATH.concat("/title")))
                .withOrganization(getString(documentXml, MANAGER_INFO_PATH.concat("/organization")))
                .build())
            .build();
    }
}
