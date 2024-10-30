package se.sundsvall.vacationdocument.integration.templating;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import se.sundsvall.vacationdocument.integration.Oauth2;

@Validated
@ConfigurationProperties(prefix = "integration.templating")
record TemplatingClientProperties(

	@NotBlank String baseUrl,

	@Valid @NotNull Oauth2 oauth2,

	@DefaultValue("10") int connectTimeoutInSeconds,

	@DefaultValue("30") int readTimeoutInSeconds) {}
