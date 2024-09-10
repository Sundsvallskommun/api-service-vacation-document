package se.sundsvall.vacationdocument;

import se.sundsvall.vacationdocument.integration.opene.EmployeeInformationBuilder;
import se.sundsvall.vacationdocument.integration.opene.ManagerInformationBuilder;
import se.sundsvall.vacationdocument.integration.opene.OpenEDocument;
import se.sundsvall.vacationdocument.integration.opene.OpenEDocumentBuilder;

public final class TestDataFactory {

    public static final String EMPLOYEE_FIRST_NAME = "someEmployeeFirstName";
    public static final String EMPLOYEE_LAST_NAME = "someEmployeeLastName";
    public static final String EMPLOYEE_EMAIL_ADDRESS = "someEmployeeEmailAddress";
    public static final String EMPLOYEE_PHONE_NUMBER = "someEmployeePhoneNumber";
    public static final String EMPLOYEE_MOBILE_NUMBER = "someEmployeeMobileNumber";
    public static final String EMPLOYEE_SSN = "someEmployeeSsn";
    public static final String EMPLOYEE_JOB_TITLE = "someEmployeeJobTitle";
    public static final String EMPLOYEE_ORGANIZATION = "someEmployeeOrganization";
    public static final String MANAGER_FIRST_NAME = "someManagerFirstName";
    public static final String MANAGER_LAST_NAME = "someManagerLastName";
    public static final String MANAGER_USERNAME = "someManagerLastName";
    public static final String MANAGER_JOB_TITLE = "someManagerJobTitle";
    public static final String MANAGER_ORGANIZATION = "someManagerOrganization";

    public static final String PARTY_ID = "somePartyId";
    public static final String OUTPUT = "someOutput";

    private TestDataFactory() { }

    public static OpenEDocument createOpenEDocument(final String id, final String name, final boolean approvedByManager) {
        return OpenEDocumentBuilder.newDocument()
            .withId(id)
            .withName(name)
            .withApprovedByManager(approvedByManager)
            .withEmployeeInformation(EmployeeInformationBuilder.newEmployeeInformation()
                .withFirstName(EMPLOYEE_FIRST_NAME)
                .withLastName(EMPLOYEE_LAST_NAME)
                .withEmailAddress(EMPLOYEE_EMAIL_ADDRESS)
                .withPhoneNumber(EMPLOYEE_PHONE_NUMBER)
                .withMobileNumber(EMPLOYEE_MOBILE_NUMBER)
                .withSsn(EMPLOYEE_SSN)
                .withJobTitle(EMPLOYEE_JOB_TITLE)
                .withOrganization(EMPLOYEE_ORGANIZATION)
                .build())
            .withManagerInformation(ManagerInformationBuilder.newManagerInformation()
                .withFirstName(MANAGER_FIRST_NAME)
                .withLastName(MANAGER_LAST_NAME)
                .withUsername(MANAGER_USERNAME)
                .withJobTitle(MANAGER_JOB_TITLE)
                .withOrganization(MANAGER_ORGANIZATION)
                .build())
            .build();
    }

}
