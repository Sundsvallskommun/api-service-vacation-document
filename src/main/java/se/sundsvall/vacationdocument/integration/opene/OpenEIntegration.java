package se.sundsvall.vacationdocument.integration.opene;

import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.vacationdocument.integration.opene.EmployeeInformationBuilder.newEmployeeInformation;
import static se.sundsvall.vacationdocument.integration.opene.ManagerInformationBuilder.newManagerInformation;
import static se.sundsvall.vacationdocument.integration.opene.OpenEDocumentBuilder.newDocument;
import static se.sundsvall.vacationdocument.integration.opene.util.XPathUtil.evaluateXPath;
import static se.sundsvall.vacationdocument.integration.opene.util.XPathUtil.getString;

import java.util.List;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

@Component
public class OpenEIntegration {

	private static final String DOCUMENTS_ID_PATH = "/FlowInstances/FlowInstance/flowInstanceID";
	private static final String DOCUMENT_ID_PATH = "/FlowInstance/Header/FlowInstanceID";
	private static final String DESCRIPTION_PATH = "/FlowInstance/Header/Flow/Name";
	private static final String STATUS_ID_PATH = "/FlowInstance/Header/Status/ID";
	private static final String EMPLOYEE_INFO_PATH = "/FlowInstance/Values/Medarbetaruppgifter";
	private static final String MANAGER_INFO_PATH = "/FlowInstance/Values/Manager";

	private final OpenEClientFactory clientFactory;
	private final OpenEClientProperties properties;

	OpenEIntegration(final OpenEClientFactory clientFactory, final OpenEClientProperties properties) {
		this.clientFactory = clientFactory;
		this.properties = properties;
	}

	public List<OpenEDocument> getDocuments(final String municipalityId, final String fromDate, final String toDate) {
		var client = clientFactory.getClient(municipalityId);

		return getDocumentIds(municipalityId, fromDate, toDate).stream()
			// Get the errand
			.map(client::getErrand)
			// Map the XML to an OpenE document
			.map(documentXml -> mapToDocument(municipalityId, documentXml))
			.toList();
	}

	List<String> getDocumentIds(final String municipalityId, final String fromDate, final String toDate) {
		var client = clientFactory.getClient(municipalityId);

		// Get the document list from OpenE
		var documentsXml = client.getErrands(getFamilyId(municipalityId), fromDate, toDate);
		// Extract the document id:s
		var result = evaluateXPath(documentsXml, DOCUMENTS_ID_PATH);
		// Trim the errand id:s, just to be safe(r)
		return result.eachText().stream()
			.map(String::trim)
			.toList();
	}

	OpenEDocument mapToDocument(final String municipalityId, final byte[] documentXml) {
		var statusId = getString(documentXml, STATUS_ID_PATH);

		return newDocument()
			.withId(getString(documentXml, DOCUMENT_ID_PATH))
			.withName(getString(documentXml, DESCRIPTION_PATH))
			.withApprovedByManager(getApprovedManagerStatusId(municipalityId).equals(statusId))
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

	String getFamilyId(final String municipalityId) {
		return ofNullable(properties.environments().get(municipalityId))
			.map(OpenEClientProperties.OpenEEnvironment::familyId)
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, String.format("No OpenE configuration exists for municipalityId %s", municipalityId)));
	}

	String getApprovedManagerStatusId(final String municipalityId) {
		return ofNullable(properties.environments().get(municipalityId))
			.map(OpenEClientProperties.OpenEEnvironment::approvedByManagerStatusId)
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, String.format("No OpenE configuration exists for municipalityId %s", municipalityId)));
	}
}
