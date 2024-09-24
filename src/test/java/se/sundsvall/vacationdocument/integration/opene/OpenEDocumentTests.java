package se.sundsvall.vacationdocument.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.vacationdocument.integration.opene.EmployeeInformationBuilder.newEmployeeInformation;
import static se.sundsvall.vacationdocument.integration.opene.ManagerInformationBuilder.newManagerInformation;
import static se.sundsvall.vacationdocument.integration.opene.OpenEDocumentBuilder.newDocument;

import org.junit.jupiter.api.Test;

class OpenEDocumentTests {

    @Test
    void testConstruction() {
        var name = "someName";
        var firstName = "someFirstName";
        var lastName = "someLastName";
        var emailAddress = "someEmailAddress";
        var phoneNumber = "somePhoneNumber";
        var mobileNumber = "someMobileNumber";
        var ssn = "someSsn";
        var formattedSsn = "som-eSsn";
        var jobTitle = "someJobTitle";
        var organization = "someOrganization";
        var username = "someUsername";

        var document = newDocument()
            .withName(name)
            .withEmployeeInformation(newEmployeeInformation()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withEmailAddress(emailAddress)
                .withPhoneNumber(phoneNumber)
                .withMobileNumber(mobileNumber)
                .withSsn(ssn)
                .withJobTitle(jobTitle)
                .withOrganization(organization)
                .build())
            .withManagerInformation(newManagerInformation()
                .withUsername(username)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withJobTitle(jobTitle)
                .withOrganization(organization)
                .build())
            .build();

        assertThat(document.name()).isEqualTo(name);
        assertThat(document.employeeInformation()).isNotNull().satisfies(employeeInformation -> {
            assertThat(employeeInformation.firstName()).isEqualTo(firstName);
            assertThat(employeeInformation.lastName()).isEqualTo(lastName);
            assertThat(employeeInformation.emailAddress()).isEqualTo(emailAddress);
            assertThat(employeeInformation.phoneNumber()).isEqualTo(phoneNumber);
            assertThat(employeeInformation.mobileNumber()).isEqualTo(mobileNumber);
            assertThat(employeeInformation.ssn()).isEqualTo(formattedSsn);
            assertThat(employeeInformation.jobTitle()).isEqualTo(jobTitle);
            assertThat(employeeInformation.organization()).isEqualTo(organization);
        });
        assertThat(document.managerInformation()).isNotNull().satisfies(managerInformation -> {
            assertThat(managerInformation.username()).isEqualTo(username);
            assertThat(managerInformation.firstName()).isEqualTo(firstName);
            assertThat(managerInformation.lastName()).isEqualTo(lastName);
            assertThat(managerInformation.jobTitle()).isEqualTo(jobTitle);
            assertThat(managerInformation.organization()).isEqualTo(organization);
        });
    }
}
