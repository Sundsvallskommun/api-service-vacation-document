package se.sundsvall.vacationdocument.integration.document;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.File;

import org.junit.jupiter.api.Test;

class DocumentMultipartFileTests {

    @Test
    void testCreation() throws Exception {
        var name = "someName";
        var data = "someData".getBytes(UTF_8);

        var documentMultipartFile = new DocumentMultipartFile(name, data);

        assertThat(documentMultipartFile.getName()).isEqualTo(name);
        assertThat(documentMultipartFile.getOriginalFilename()).isEqualTo(name);
        assertThat(documentMultipartFile.getContentType()).isEqualTo(APPLICATION_PDF_VALUE);
        assertThat(documentMultipartFile.isEmpty()).isFalse();
        assertThat(documentMultipartFile.getSize()).isEqualTo(data.length);
        assertThat(documentMultipartFile.getBytes()).isEqualTo(data);
        assertThat(documentMultipartFile.getInputStream().readAllBytes()).isEqualTo(data);

        var file = File.createTempFile("__test", null);
        documentMultipartFile.transferTo(file);

        assertThat(file).exists();
        assertThat(readFileToByteArray(file)).isEqualTo(data);
    }
}
