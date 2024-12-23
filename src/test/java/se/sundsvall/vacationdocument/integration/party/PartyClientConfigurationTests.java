package se.sundsvall.vacationdocument.integration.party;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.vacationdocument.integration.party.PartyClientConfiguration.CLIENT_ID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.vacationdocument.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class PartyClientConfigurationTests {

	@Mock
	private ClientRegistration mockClientRegistration;

	@Mock
	private ClientRegistration.Builder mockClientRegistrationBuilder;

	@Mock
	private FeignBuilderCustomizer mockFeignBuilderCustomizer;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Captor
	private ArgumentCaptor<ProblemErrorDecoder> errorDecoderCaptor;

	@Autowired
	private PartyClientProperties mockProperties;

	@Test
	void testFeignBuilderMultiCustomizer() {
		var configuration = new PartyClientConfiguration();

		try (var mockFeignMultiCustomizer = mockStatic(FeignMultiCustomizer.class);
			var staticMockClientRegistration = mockStatic(ClientRegistration.class)) {
			mockFeignMultiCustomizer.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);
			staticMockClientRegistration.when(() -> ClientRegistration.withRegistrationId(CLIENT_ID)).thenReturn(mockClientRegistrationBuilder);

			when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(mockFeignBuilderCustomizer);
			when(mockClientRegistrationBuilder.tokenUri("someTokenUrl")).thenReturn(mockClientRegistrationBuilder);
			when(mockClientRegistrationBuilder.clientId("someClientId")).thenReturn(mockClientRegistrationBuilder);
			when(mockClientRegistrationBuilder.clientSecret("someClientSecret")).thenReturn(mockClientRegistrationBuilder);
			when(mockClientRegistrationBuilder.authorizationGrantType(any(AuthorizationGrantType.class))).thenReturn(mockClientRegistrationBuilder);
			when(mockClientRegistrationBuilder.build()).thenReturn(mockClientRegistration);

			var customizer = configuration.feignBuilderCustomizer(mockProperties);

			verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
			verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(same(mockClientRegistration));
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(6, 12);
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(errorDecoderCaptor.getValue()).hasFieldOrPropertyWithValue("integrationName", CLIENT_ID);
			assertThat(customizer).isSameAs(mockFeignBuilderCustomizer);
		}
	}
}
