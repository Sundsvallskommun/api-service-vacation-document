package se.sundsvall.vacationdocument.integration.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.vacationdocument.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class DocumentClientPropertiesTests {

	@Autowired
	private DocumentClientProperties properties;

	@Test
	void verifyProperties() {
		assertThat(properties.baseUrl()).isEqualTo("someBaseUrl");
		assertThat(properties.oauth2()).isNotNull().satisfies(oauth2 -> {
			assertThat(oauth2.tokenUrl()).isEqualTo("someTokenUrl");
			assertThat(oauth2.clientId()).isEqualTo("someClientId");
			assertThat(oauth2.clientSecret()).isEqualTo("someClientSecret");
		});
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(4);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(13);
	}
}
