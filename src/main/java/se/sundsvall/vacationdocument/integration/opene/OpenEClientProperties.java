package se.sundsvall.vacationdocument.integration.opene;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.open-e")
public record OpenEClientProperties(Map<String, OpenEEnvironment> environments) {

	public record OpenEEnvironment(

		@Valid @NotNull Scheduling scheduling,

		@NotBlank String baseUrl,

		@NotBlank String familyId,

		@NotBlank String approvedByManagerStatusId,

		@NotBlank String username,

		@NotBlank String password,

		@NotBlank String templateId,

		@DefaultValue("5") int connectTimeoutInSeconds,

		@DefaultValue("30") int readTimeoutInSeconds) {

		public record Scheduling(

			@DefaultValue("true") boolean enabled,

			@NotBlank String cronExpression,

			@DefaultValue("PT2M") Duration lockAtMostFor) {}
	}
}
