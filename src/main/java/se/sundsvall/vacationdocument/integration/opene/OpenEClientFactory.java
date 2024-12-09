package se.sundsvall.vacationdocument.integration.opene;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.soap.SOAPErrorDecoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

@Component
class OpenEClientFactory {

	private static final Logger LOG = LoggerFactory.getLogger(OpenEClientFactory.class);

	private final Map<String, OpenEClient> clients = new HashMap<>();

	OpenEClientFactory(final ApplicationContext applicationContext, final OpenEClientProperties openEClientProperties) {
		openEClientProperties.environments().forEach((municipalityId, environment) -> {
			createClient(applicationContext, municipalityId, environment);
		});
	}

	OpenEClient getClient(final String municipalityId) {
		return ofNullable(clients.get(municipalityId))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, String.format("No OpenE client exists for municipalityId %s", municipalityId)));
	}

	void createClient(final ApplicationContext applicationContext, final String municipalityId,
		final OpenEClientProperties.OpenEEnvironment environment) {
		var clientName = "oep-%s".formatted(municipalityId);
		var client = new FeignClientBuilder(applicationContext)
			.forType(OpenEClient.class, clientName)
			.customize(builder -> builder
				.errorDecoder(new SOAPErrorDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(environment.username(), environment.password()))
				.options(new Request.Options(environment.connectTimeoutInSeconds(), SECONDS, environment.readTimeoutInSeconds(), SECONDS, true)))
			.url(environment.baseUrl())
			.build();
		clients.put(municipalityId, client);

		LOG.info("Created OpenE client for municipalityId {} ({})", municipalityId, clientName);
	}
}
