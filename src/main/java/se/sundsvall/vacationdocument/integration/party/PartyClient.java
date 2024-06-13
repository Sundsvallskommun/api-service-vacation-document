package se.sundsvall.vacationdocument.integration.party;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.vacationdocument.integration.party.PartyIntegration.CLIENT_ID;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import generated.se.sundsvall.party.PartyType;

@FeignClient(
    name = CLIENT_ID,
    configuration = PartyIntegrationConfiguration.class,
    url = "${integration.party.base-url}"
)
interface PartyClient {

    @GetMapping(
        path = "/{type}/{legalId}/partyId",
        produces = { TEXT_PLAIN_VALUE, APPLICATION_PROBLEM_JSON_VALUE }
    )
    Optional<String> getPartyId(@PathVariable("type") PartyType partyType, @PathVariable("legalId") String legalId);
}