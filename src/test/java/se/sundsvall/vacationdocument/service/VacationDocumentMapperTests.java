package se.sundsvall.vacationdocument.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_EMAIL_ADDRESS;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_FIRST_NAME;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_JOB_TITLE;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_LAST_NAME;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_MOBILE_NUMBER;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_ORGANIZATION;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_PHONE_NUMBER;
import static se.sundsvall.vacationdocument.TestDataFactory.EMPLOYEE_SSN;
import static se.sundsvall.vacationdocument.TestDataFactory.MANAGER_FIRST_NAME;
import static se.sundsvall.vacationdocument.TestDataFactory.MANAGER_JOB_TITLE;
import static se.sundsvall.vacationdocument.TestDataFactory.MANAGER_LAST_NAME;
import static se.sundsvall.vacationdocument.TestDataFactory.MANAGER_ORGANIZATION;
import static se.sundsvall.vacationdocument.TestDataFactory.MANAGER_USERNAME;
import static se.sundsvall.vacationdocument.TestDataFactory.createOpenEDocument;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.EMAIL_ADDRESS;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.EMPLOYEE_INFORMATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.FIRST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.JOB_TITLE;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.LAST_NAME;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.MANAGER_INFORMATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.MOBILE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.ORGANIZATION;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.PHONE_NUMBER;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.SSN;
import static se.sundsvall.vacationdocument.service.VacationDocumentConstants.USERNAME;

import org.junit.jupiter.api.Test;

class VacationDocumentMapperTests {

    @Test
    void testMapToRenderRequestParameters() {
        var parameters = VacationDocumentMapper.mapToRenderRequestParameters(createOpenEDocument());

        assertThat(parameters).containsKeys(EMPLOYEE_INFORMATION, MANAGER_INFORMATION);
        assertThat(parameters.get(EMPLOYEE_INFORMATION))
                .asInstanceOf(MAP)
                .satisfies(employeeInformation -> {
            assertThat(employeeInformation).containsEntry(FIRST_NAME, EMPLOYEE_FIRST_NAME);
            assertThat(employeeInformation).containsEntry(LAST_NAME, EMPLOYEE_LAST_NAME);
            assertThat(employeeInformation).containsEntry(EMAIL_ADDRESS, EMPLOYEE_EMAIL_ADDRESS);
            assertThat(employeeInformation).containsEntry(PHONE_NUMBER, EMPLOYEE_PHONE_NUMBER);
            assertThat(employeeInformation).containsEntry(MOBILE_NUMBER, EMPLOYEE_MOBILE_NUMBER);
            assertThat(employeeInformation).containsEntry(SSN, EMPLOYEE_SSN);
            assertThat(employeeInformation).containsEntry(JOB_TITLE, EMPLOYEE_JOB_TITLE);
            assertThat(employeeInformation).containsEntry(ORGANIZATION, EMPLOYEE_ORGANIZATION);
        });

        assertThat(parameters.get(MANAGER_INFORMATION))
            .asInstanceOf(MAP)
            .satisfies(managerInformation -> {
                assertThat(managerInformation).containsEntry(FIRST_NAME, MANAGER_FIRST_NAME);
                assertThat(managerInformation).containsEntry(LAST_NAME, MANAGER_LAST_NAME);
                assertThat(managerInformation).containsEntry(USERNAME, MANAGER_USERNAME);
                assertThat(managerInformation).containsEntry(JOB_TITLE, MANAGER_JOB_TITLE);
                assertThat(managerInformation).containsEntry(ORGANIZATION, MANAGER_ORGANIZATION);
            });
    }
}
