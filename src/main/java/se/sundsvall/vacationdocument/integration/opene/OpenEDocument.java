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
            String organization) {

        @Override
        public String ssn() {
            var length = ssn.length();

            return ssn.substring(0, length - 4) + "-" + ssn.substring(length - 4);
        }

        public String name() {
            return firstName + " " + lastName;
        }
    }

    @Builder(setterPrefix = "with", factoryMethod = "newManagerInformation")
    public record ManagerInformation(

            String username,
            String firstName,
            String lastName,
            String jobTitle,
            String organization) {

        public String name() {
            return firstName + " " + lastName;
        }
    }
}
