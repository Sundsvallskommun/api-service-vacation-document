package se.sundsvall.vacationdocument.integration.opene;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.open-e")
record OpenEClientProperties(

        @NotBlank
        String baseUrl,

        @NotBlank
        String familyId,

        @NotBlank
        String approvedByManagerStatusId,

        @NotBlank
        String username,

        @NotBlank
        String password,

        @DefaultValue("5")
        int connectTimeoutInSeconds,

        @DefaultValue("30")
        int readTimeoutInSeconds) { }
