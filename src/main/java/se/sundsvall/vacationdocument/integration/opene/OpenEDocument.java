package se.sundsvall.vacationdocument.integration.opene;

import org.jilt.Builder;

@Builder(setterPrefix = "with", factoryMethod = "newDocument")
public record OpenEDocument(
        String id,
        String name,
        boolean approvedByManager,
        EmployeeInformation employeeInformation,
        ManagerInformation managerInformation) {

    @Builder(setterPrefix = "with", factoryMethod = "newEmployeeInformation")
    public record EmployeeInformation(

        String firstName,
        String lastName,
        String emailAddress,
        String phoneNumber,
        String mobileNumber,
        String ssn,
        String jobTitle,
        String organization) { }

    @Builder(setterPrefix = "with", factoryMethod = "newManagerInformation")
    public record ManagerInformation(

        String username,
        String firstName,
        String lastName,
        String jobTitle,
        String organization) { }
}
