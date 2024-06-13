package se.sundsvall.vacationdocument.integration.party;

import static generated.se.sundsvall.party.PartyType.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;

import se.sundsvall.dept44.exception.ClientProblem;

@ExtendWith(MockitoExtension.class)
class PartyIntegrationTests {

    @Mock
    private PartyClient mockPartyClient;

    @InjectMocks
    private PartyIntegration partyIntegration;

    @Test
    void testGetPartyId() {
        var legalId = "someLegalId";

        when(mockPartyClient.getPartyId(PRIVATE, legalId)).thenReturn(Optional.of("somePartyId"));

        var result = partyIntegration.getPartyId(legalId);

        assertThat(result).isNotNull().hasValue("somePartyId");

        verify(mockPartyClient).getPartyId(PRIVATE, legalId);
    }

    @Test
    void testGetPartyIdWhenExceptionIsThrown() {
        var legalId = "someLegalId";

        when(mockPartyClient.getPartyId(PRIVATE, legalId)).thenThrow(new ClientProblem(Status.NOT_FOUND, "No legal id found"));

        var result = partyIntegration.getPartyId(legalId);

        assertThat(result).isEmpty();

        verify(mockPartyClient).getPartyId(PRIVATE, legalId);
    }
}
