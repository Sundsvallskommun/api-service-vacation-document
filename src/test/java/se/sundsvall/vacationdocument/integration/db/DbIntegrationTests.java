package se.sundsvall.vacationdocument.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.DETAIL;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.DOCUMENT_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_EXISTS_BY_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_INSERT;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_UPDATE;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.STATUS;
import static se.sundsvall.vacationdocument.model.DocumentStatus.PROCESSING;

import java.sql.Types;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import se.sundsvall.vacationdocument.model.DocumentStatus;

@ExtendWith(MockitoExtension.class)
class DbIntegrationTests {

    @Mock
    private NamedParameterJdbcTemplate mockJdbcTemplate;

    @InjectMocks
    private DbIntegration dbIntegration;

    @Test
    void existsById() {
        when(mockJdbcTemplate.queryForObject(eq(SQL_EXISTS_BY_ID), anyMap(), eq(Long.class))).thenReturn(1L);

        var result = dbIntegration.existsById("someDocumentId");

        assertThat(result).isTrue();

        verify(mockJdbcTemplate).queryForObject(eq(SQL_EXISTS_BY_ID), anyMap(), eq(Long.class));
    }

    @Test
    void saveDocument() {
        dbIntegration.saveDocument("someDocumentId", PROCESSING);

        verify(mockJdbcTemplate).update(eq(SQL_INSERT), any(MapSqlParameterSource.class));
    }

    @Test
    void updateDocument() {
        dbIntegration.updateDocument("someDocumentId", PROCESSING);

        verify(mockJdbcTemplate).update(eq(SQL_UPDATE), any(MapSqlParameterSource.class));
    }

    @Test
    void mapToSqlParameterSourceWithDetail() {
        var documentId = "someDocumentId";
        var status = DocumentStatus.FAILED;
        var detail = "someDetail";

        var result = dbIntegration.mapToSqlParameterSource(documentId, status, Optional.of(detail));

        assertThat(result.getValue(DOCUMENT_ID)).isEqualTo(documentId);
        assertThat(result.getValue(STATUS)).isEqualTo(status);
        assertThat(result.getSqlType(STATUS)).isEqualTo(Types.VARCHAR);
        assertThat(result.getValue(DETAIL)).isEqualTo(detail);
    }

    @Test
    void mapToSqlParameterSourceWithoutDetail() {
        var documentId = "someDocumentId";
        var status = DocumentStatus.FAILED;

        var result = dbIntegration.mapToSqlParameterSource(documentId, status, Optional.empty());

        assertThat(result.getValue(DOCUMENT_ID)).isEqualTo(documentId);
        assertThat(result.getValue(STATUS)).isEqualTo(status);
        assertThat(result.getSqlType(STATUS)).isEqualTo(Types.VARCHAR);
        assertThat(result.hasValue(DETAIL)).isFalse();
    }
}
