package se.sundsvall.vacationdocument.service;

import static java.util.Optional.ofNullable;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.EMAIL_ADDRESS;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.EMPLOYEE_INFORMATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.FIRST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.JOB_TITLE;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.LAST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.MANAGER_INFORMATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.MOBILE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.ORGANIZATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.PARTY_ID;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.PHONE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.SSN;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.TIMESTAMP;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.USERNAME;

import generated.se.sundsvall.document.Confidentiality;
import generated.se.sundsvall.document.DocumentCreateRequest;
import generated.se.sundsvall.document.DocumentMetadata;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.sundsvall.vacationdocument.integration.opene.OpenEDocument;

final class VacationDocumentMapper {

	private VacationDocumentMapper() {}

	static Map<String, Object> mapToRenderRequestParameters(final OpenEDocument document) {
		var employeeInformation = new HashMap<String, String>();

		ofNullable(document.employeeInformation()).ifPresent(employee -> {
			employeeInformation.put(NAME, employee.name());
			employeeInformation.put(SSN, employee.ssn());

			ofNullable(employee.emailAddress()).ifPresent(value -> employeeInformation.put(EMAIL_ADDRESS, value));
			ofNullable(employee.phoneNumber()).ifPresent(value -> employeeInformation.put(PHONE_NUMBER, value));
			ofNullable(employee.mobileNumber()).ifPresent(value -> employeeInformation.put(MOBILE_NUMBER, value));
			ofNullable(employee.jobTitle()).ifPresent(value -> employeeInformation.put(JOB_TITLE, value));
			ofNullable(employee.organization()).ifPresent(value -> employeeInformation.put(ORGANIZATION, value));
		});

		var managerInformation = new HashMap<String, String>();

		ofNullable(document.managerInformation()).ifPresent(manager -> {
			managerInformation.put(NAME, manager.name());

			ofNullable(manager.username()).ifPresent(value -> managerInformation.put(USERNAME, value));
			ofNullable(manager.jobTitle()).ifPresent(value -> managerInformation.put(JOB_TITLE, value));
			ofNullable(manager.organization()).ifPresent(value -> managerInformation.put(ORGANIZATION, value));
		});

		return Map.of(
			TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")),
			EMPLOYEE_INFORMATION, employeeInformation,
			MANAGER_INFORMATION, managerInformation);
	}

	static DocumentCreateRequest mapToDocumentCreateRequest(final String partyId, final OpenEDocument document) {
		return new DocumentCreateRequest()
			.description(document.name())
			.confidentiality(new Confidentiality().confidential(false))
			.archive(false)
			.createdBy(document.employeeInformation().firstName() + " " + document.employeeInformation().lastName())
			.metadataList(List.of(
				// Employee information
				new DocumentMetadata().key(employeeKey(PARTY_ID)).value(partyId),
				new DocumentMetadata().key(employeeKey(FIRST_NAME)).value(document.employeeInformation().firstName()),
				new DocumentMetadata().key(employeeKey(LAST_NAME)).value(document.employeeInformation().lastName()),
				new DocumentMetadata().key(employeeKey(EMAIL_ADDRESS)).value(document.employeeInformation().emailAddress()),
				new DocumentMetadata().key(employeeKey(PHONE_NUMBER)).value(document.employeeInformation().phoneNumber()),
				new DocumentMetadata().key(employeeKey(MOBILE_NUMBER)).value(document.employeeInformation().mobileNumber()),
				new DocumentMetadata().key(employeeKey(JOB_TITLE)).value(document.employeeInformation().jobTitle()),
				new DocumentMetadata().key(employeeKey(ORGANIZATION)).value(document.employeeInformation().organization()),
				// Manager information
				new DocumentMetadata().key(managerKey(FIRST_NAME)).value(document.managerInformation().firstName()),
				new DocumentMetadata().key(managerKey(LAST_NAME)).value(document.managerInformation().lastName()),
				new DocumentMetadata().key(managerKey(USERNAME)).value(document.managerInformation().username()),
				new DocumentMetadata().key(managerKey(JOB_TITLE)).value(document.managerInformation().jobTitle()),
				new DocumentMetadata().key(managerKey(ORGANIZATION)).value(document.managerInformation().organization())));
	}

	static String employeeKey(final String key) {
		return EMPLOYEE_INFORMATION.concat(".").concat(key);
	}

	static String managerKey(final String key) {
		return MANAGER_INFORMATION.concat(".").concat(key);
	}
}
