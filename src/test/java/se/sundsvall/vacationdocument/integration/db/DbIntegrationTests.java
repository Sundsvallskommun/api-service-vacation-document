package se.sundsvall.vacationdocument.integration.db;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.DOCUMENT_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_FIND_ALL;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_FIND_BY_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_INSERT;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_UPDATE;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.STATUS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import se.sundsvall.vacationdocument.model.Document;
import se.sundsvall.vacationdocument.model.DocumentStatus;

@ExtendWith(MockitoExtension.class)
class DbIntegrationTests {

    @Mock
    private NamedParameterJdbcTemplate mockJdbcTemplate;

    @InjectMocks
    private DbIntegration dbIntegration;

    @Test
    void getDocuments() {
        dbIntegration.getDocuments();

        verify(mockJdbcTemplate).queryForStream(eq(SQL_FIND_ALL), eq(emptyMap()), ArgumentMatchers.<RowMapper<Document>>any());
    }

    @Test
    void getDocument() {
        var map = Map.of(DOCUMENT_ID, "someDocumentId");

        dbIntegration.getDocument("someDocumentId");

        verify(mockJdbcTemplate).queryForStream(eq(SQL_FIND_BY_ID), eq(map), ArgumentMatchers.<RowMapper<Document>>any());
    }

    @Test
    void saveDocument() {
        dbIntegration.saveDocument(new Document("someDocumentId", DocumentStatus.NEW));

        verify(mockJdbcTemplate).update(eq(SQL_INSERT), any(MapSqlParameterSource.class));
    }

    @Test
    void updateDocument() {
        dbIntegration.updateDocument(new Document("someDocumentId", DocumentStatus.NEW));

        verify(mockJdbcTemplate).update(eq(SQL_UPDATE), any(MapSqlParameterSource.class));
    }

    @Test
    void mapToSqlParameterSource() {
        var documentId = "someDocumentId";
        var status = DocumentStatus.FAILED;

        var result = dbIntegration.mapToSqlParameterSource(new Document(documentId, status));

        assertThat(result.getValue(DOCUMENT_ID)).isEqualTo(documentId);
        assertThat(result.getValue(STATUS)).isEqualTo(status);
        assertThat(result.getSqlType(STATUS)).isEqualTo(Types.VARCHAR);
    }

    @Test
    void mapToDocument() throws SQLException {
        var documentId = "someDocumentId";
        var status = DocumentStatus.DONE;

        var mockResultSet = mock(ResultSet.class);
        when(mockResultSet.getString(DOCUMENT_ID)).thenReturn(documentId);
        when(mockResultSet.getString(STATUS)).thenReturn(status.name());

        var result = dbIntegration.mapToDocument(mockResultSet, 0);

        assertThat(result.documentId()).isEqualTo(documentId);
        assertThat(result.status()).isEqualTo(status);

        verify(mockResultSet).getString(DOCUMENT_ID);
        verify(mockResultSet).getString(STATUS);
    }
}
