package se.sundsvall.vacationdocument.integration.document;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static se.sundsvall.vacationdocument.integration.document.DocumentClientConfiguration.CLIENT_ID;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import generated.se.sundsvall.document.DocumentCreateRequest;

@FeignClient(
    name = CLIENT_ID,
    configuration = DocumentClientConfiguration.class,
    url = "${integration.document.base-url}"
)
public interface DocumentClient {

    @PostMapping(
        path = "/{municipalityId}/documents",
        consumes = { MULTIPART_FORM_DATA_VALUE },
        produces = { ALL_VALUE, APPLICATION_PROBLEM_JSON_VALUE }
    )
    ResponseEntity<Void> createDocument(
        @PathVariable("municipalityId") String municipalityId,
        @RequestPart("document") DocumentCreateRequest document,
        @RequestPart("documentFiles") List<DocumentMultipartFile> documentFiles);
}