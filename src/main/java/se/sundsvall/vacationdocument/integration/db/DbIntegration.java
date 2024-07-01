package se.sundsvall.vacationdocument.integration.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import se.sundsvall.vacationdocument.model.Document;
import se.sundsvall.vacationdocument.model.DocumentStatus;

@Component
public class DbIntegration {

    static final String DOCUMENT_ID = "document_id";
    static final String STATUS = "status";

    static final String SQL_FIND_ALL = "SELECT * FROM document";
    static final String SQL_FIND_BY_ID = "SELECT * FROM document d WHERE d.document_id = :document_id";
    static final String SQL_INSERT = "INSERT INTO document (document_id, status) VALUES (:document_id, :status)";
    static final String SQL_UPDATE = "UPDATE document d SET d.status = :status WHERE d.document_id = :document_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    DbIntegration(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Document> getDocuments() {
        return jdbcTemplate.queryForStream(SQL_FIND_ALL, Map.of(), this::mapToDocument).toList();
    }

    public Document getDocument(final String documentId) {
        return jdbcTemplate.queryForStream(SQL_FIND_BY_ID, Map.of(DOCUMENT_ID, documentId), this::mapToDocument)
            .findFirst()
            .orElse(null);
    }

    public void saveDocument(final Document document) {
        jdbcTemplate.update(SQL_INSERT, mapToSqlParameterSource(document));
    }

    public void updateDocument(final Document document) {
        jdbcTemplate.update(SQL_UPDATE, mapToSqlParameterSource(document));
    }

    SqlParameterSource mapToSqlParameterSource(final Document document) {
        return new MapSqlParameterSource()
            .addValue(DOCUMENT_ID, document.documentId())
            .addValue(STATUS, document.status(), Types.VARCHAR);
    }

    Document mapToDocument(final ResultSet rs, final int rowNum) throws SQLException {
        return new Document(rs.getString(DOCUMENT_ID), DocumentStatus.valueOf(rs.getString(STATUS)));
    }
}
