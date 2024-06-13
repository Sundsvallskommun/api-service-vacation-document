package se.sundsvall.vacationdocument.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Oauth2Tests {

    @Test
    void testConstructorAndGetters() {
        var tokenUrl = "someTokenUrl";
        var clientId = "someClientId";
        var clientSecret = "someClientSecret";

        assertThat(new Oauth2(tokenUrl, clientId, clientSecret)).satisfies(oauth2 -> {
            assertThat(oauth2.tokenUrl()).isEqualTo(tokenUrl);
            assertThat(oauth2.clientId()).isEqualTo(clientId);
            assertThat(oauth2.clientSecret()).isEqualTo(clientSecret);
        });
    }
}
