package se.sundsvall.vacationdocument.integration.opene;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;

@Import(FeignConfiguration.class)
@EnableConfigurationProperties(OpenEClientProperties.class)
class OpenEClientConfiguration {

}
