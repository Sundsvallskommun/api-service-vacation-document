package se.sundsvall.vacationdocument.integration.templating;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.vacationdocument.integration.templating.TemplatingClientConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import generated.se.sundsvall.templating.RenderRequest;
import generated.se.sundsvall.templating.RenderResponse;

@FeignClient(
	name = CLIENT_ID,
	configuration = TemplatingClientConfiguration.class,
	url = "${integration.templating.base-url}")
public interface TemplatingClient {

	@PostMapping(
		path = "/{municipalityId}/render/pdf",
		produces = {
			APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
		})
	RenderResponse renderPdf(@PathVariable("municipalityId") String municipalityId, @RequestBody RenderRequest request);
}
