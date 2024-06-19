package se.sundsvall.vacationdocument.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.vacationdocument.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class OpenEClientPropertiesTests {

    @Autowired
    private OpenEClientProperties properties;

    @Test
    void verifyProperties() {
        assertThat(properties.baseUrl()).isEqualTo("someBaseUrl");
        assertThat(properties.username()).isEqualTo("someUsername");
        assertThat(properties.password()).isEqualTo("somePassword");
        assertThat(properties.familyId()).isEqualTo("someFamilyId");
        assertThat(properties.approvedByManagerStatusId()).isEqualTo("someStatusId");
        assertThat(properties.connectTimeoutInSeconds()).isEqualTo(7);
        assertThat(properties.readTimeoutInSeconds()).isEqualTo(11);
    }
}
