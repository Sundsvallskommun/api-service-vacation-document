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
		assertThat(properties).isNotNull();
		assertThat(properties.environments()).hasSize(1);
		assertThat(properties.environments().get("1984")).satisfies(openEEnvironment -> {
			assertThat(openEEnvironment.baseUrl()).isEqualTo("someBaseUrl");
			assertThat(openEEnvironment.username()).isEqualTo("someUsername");
			assertThat(openEEnvironment.password()).isEqualTo("somePassword");
			assertThat(openEEnvironment.familyId()).isEqualTo("someFamilyId");
			assertThat(openEEnvironment.approvedByManagerStatusId()).isEqualTo("someStatusId");
			assertThat(openEEnvironment.templateId()).isEqualTo("someTemplateId");
			assertThat(openEEnvironment.connectTimeoutInSeconds()).isEqualTo(7);
			assertThat(openEEnvironment.readTimeoutInSeconds()).isEqualTo(11);
		});
	}
}
