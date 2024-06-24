package se.sundsvall.vacationdocument.integration.document;

import static java.io.InputStream.nullInputStream;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;

public record DocumentMultipartFile(String name, byte[] data) implements MultipartFile {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return name;
    }

    @Override
    public String getContentType() {
        return APPLICATION_PDF_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.isEmpty(data);
    }

    @Override
    public long getSize() {
        return isEmpty() ? 0 : data.length;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

    @Override
    public InputStream getInputStream() {
        return isEmpty() ? nullInputStream() : new ByteArrayInputStream(data);
    }

    @Override
    public void transferTo(final File dest) throws IOException, IllegalStateException {
        if (!isEmpty()) {
            try (var out = new FileOutputStream(dest)) {
                out.write(data);
            }
        }
    }
}
