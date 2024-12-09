package se.sundsvall.vacationdocument.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;

import feign.Client;
import feign.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.dept44.configuration.TruststoreConfiguration;

@SpringBootTest(classes = {
	TruststoreConfiguration.class, OpenEClientConfiguration.class
})
@ActiveProfiles("junit")
class OpenEClientConfigurationTests {

	@Autowired
	private OpenEClientProperties openEClientProperties;

	@Autowired
	private Logger.Level feignLoggerLevel;

	@Autowired
	private Client feignOkHttpClient;

	@Test
	void testAutowiring() {
		assertThat(openEClientProperties).isNotNull();
		assertThat(feignLoggerLevel).isNotNull();
		assertThat(feignOkHttpClient).isNotNull();
	}
}
