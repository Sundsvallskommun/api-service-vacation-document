package se.sundsvall.vacationdocument.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.vacationdocument.integration.opene.OpenEClientConfiguration.CLIENT_ID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.vacationdocument.Application;

import feign.auth.BasicAuthRequestInterceptor;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class OpenEClientConfigurationTests {

    @Mock
    private OpenEClientProperties mockProperties;

    @Mock
    private FeignBuilderCustomizer mockFeignBuilderCustomizer;

    @Spy
    private FeignMultiCustomizer feignMultiCustomizerSpy;

    @Captor
    private ArgumentCaptor<ProblemErrorDecoder> errorDecoderCaptor;

    @Test
    void testFeignBuilderMultiCustomizer() {
        when(mockProperties.username()).thenReturn("someUsername");
        when(mockProperties.password()).thenReturn("somePassword");
        when(mockProperties.connectTimeoutInSeconds()).thenReturn(9);
        when(mockProperties.readTimeoutInSeconds()).thenReturn(17);

        var configuration = new OpenEClientConfiguration();

        try (var mockFeignMultiCustomizer = mockStatic(FeignMultiCustomizer.class)) {
            mockFeignMultiCustomizer.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

            when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(mockFeignBuilderCustomizer);

            var customizer = configuration.feignBuilderCustomizer(mockProperties);

            verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
            verify(feignMultiCustomizerSpy).withRequestInterceptor(any(BasicAuthRequestInterceptor.class));
            verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(9, 17);
            verify(feignMultiCustomizerSpy).composeCustomizersToOne();

            verify(mockProperties).username();
            verify(mockProperties).password();
            verify(mockProperties).connectTimeoutInSeconds();
            verify(mockProperties).readTimeoutInSeconds();

            assertThat(errorDecoderCaptor.getValue()).hasFieldOrPropertyWithValue("integrationName", CLIENT_ID);
            assertThat(customizer).isSameAs(mockFeignBuilderCustomizer);
        }
    }
}
