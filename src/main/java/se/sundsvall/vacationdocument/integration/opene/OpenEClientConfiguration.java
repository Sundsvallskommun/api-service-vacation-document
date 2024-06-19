package se.sundsvall.vacationdocument.integration.opene;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import feign.auth.BasicAuthRequestInterceptor;

@Import(FeignConfiguration.class)
class OpenEClientConfiguration {

    static final String CLIENT_ID = "open-e";

    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer(final OpenEClientProperties properties) {
        return FeignMultiCustomizer.create()
            .withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
            .withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.password()))
            .withRequestTimeoutsInSeconds(properties.connectTimeoutInSeconds(), properties.readTimeoutInSeconds())
            .composeCustomizersToOne();
    }
}
