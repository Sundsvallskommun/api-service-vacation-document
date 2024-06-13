package se.sundsvall.vacationdocument.integration.party;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.vacationdocument.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class PartyIntegrationPropertiesTests {

    @Autowired
    private PartyIntegrationProperties properties;

    @Test
    void verifyProperties() {
        assertThat(properties.baseUrl()).isEqualTo("someBaseUrl");
        assertThat(properties.oauth2()).isNotNull().satisfies(oauth2 -> {
            assertThat(oauth2.tokenUrl()).isEqualTo("someTokenUrl");
            assertThat(oauth2.clientId()).isEqualTo("someClientId");
            assertThat(oauth2.clientSecret()).isEqualTo("someClientSecret");
        });
        assertThat(properties.connectTimeoutInSeconds()).isEqualTo(6);
        assertThat(properties.readTimeoutInSeconds()).isEqualTo(12);
    }
}
