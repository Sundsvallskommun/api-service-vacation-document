package se.sundsvall.vacationdocument.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.DETAIL;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.DOCUMENT_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.MUNICIPALITY_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_EXISTS_BY_ID;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_INSERT;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.SQL_UPDATE;
import static se.sundsvall.vacationdocument.integration.db.DbIntegration.STATUS;
import static se.sundsvall.vacationdocument.model.DocumentStatus.PROCESSING;

import java.sql.Types;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

	@Captor
	private ArgumentCaptor<Map<String, Object>> parameterMapCaptor;
	@Captor
	private ArgumentCaptor<MapSqlParameterSource> parameterSourceCaptor;

	@InjectMocks
	private DbIntegration dbIntegration;

	@Test
	void existsById() {
		var documentId = "someDocumentId";
		var municipalityId = "someMunicipalityId";

		when(mockJdbcTemplate.queryForObject(eq(SQL_EXISTS_BY_ID), anyMap(), eq(Long.class))).thenReturn(1L);

		var result = dbIntegration.existsById(documentId, municipalityId);

		assertThat(result).isTrue();

		verify(mockJdbcTemplate).queryForObject(eq(SQL_EXISTS_BY_ID), parameterMapCaptor.capture(), eq(Long.class));

		var parameterMap = parameterMapCaptor.getValue();
		assertThat(parameterMap)
			.containsEntry(DOCUMENT_ID, documentId)
			.containsEntry(MUNICIPALITY_ID, municipalityId);
	}

	@Test
	void saveDocument() {
		var documentId = "someDocumentId";
		var municipalityId = "someMunicipalityId";
		var status = PROCESSING;

		dbIntegration.saveDocument(documentId, municipalityId, status);

		verify(mockJdbcTemplate).update(eq(SQL_INSERT), parameterSourceCaptor.capture());

		var parameterSource = parameterSourceCaptor.getValue();
		assertThat(parameterSource.getValue(DOCUMENT_ID)).isEqualTo(documentId);
		assertThat(parameterSource.getValue(MUNICIPALITY_ID)).isEqualTo(municipalityId);
		assertThat(parameterSource.getValue(STATUS)).isEqualTo(status);
	}

	@Test
	void updateDocument() {
		var documentId = "someDocumentId";
		var municipalityId = "someMunicipalityId";
		var status = PROCESSING;

		dbIntegration.updateDocument(documentId, municipalityId, status);

		verify(mockJdbcTemplate).update(eq(SQL_UPDATE), parameterSourceCaptor.capture());

		var parameterSource = parameterSourceCaptor.getValue();
		assertThat(parameterSource.getValue(DOCUMENT_ID)).isEqualTo(documentId);
		assertThat(parameterSource.getValue(MUNICIPALITY_ID)).isEqualTo(municipalityId);
		assertThat(parameterSource.getValue(STATUS)).isEqualTo(status);
	}

	@Test
	void mapToSqlParameterSourceWithDetail() {
		var documentId = "someDocumentId";
		var municipalityId = "someMunicipalityId";
		var status = DocumentStatus.FAILED;
		var detail = "someDetail";

		var result = dbIntegration.mapToSqlParameterSource(documentId, municipalityId, status, Optional.of(detail));

		assertThat(result.getValue(DOCUMENT_ID)).isEqualTo(documentId);
		assertThat(result.getValue(MUNICIPALITY_ID)).isEqualTo(municipalityId);
		assertThat(result.getValue(STATUS)).isEqualTo(status);
		assertThat(result.getSqlType(STATUS)).isEqualTo(Types.VARCHAR);
		assertThat(result.getValue(DETAIL)).isEqualTo(detail);
	}

	@Test
	void mapToSqlParameterSourceWithoutDetail() {
		var documentId = "someDocumentId";
		var municipalityId = "someMunicipalityId";
		var status = DocumentStatus.FAILED;

		var result = dbIntegration.mapToSqlParameterSource(documentId, municipalityId, status, Optional.empty());

		assertThat(result.getValue(DOCUMENT_ID)).isEqualTo(documentId);
		assertThat(result.getValue(MUNICIPALITY_ID)).isEqualTo(municipalityId);
		assertThat(result.getValue(STATUS)).isEqualTo(status);
		assertThat(result.getSqlType(STATUS)).isEqualTo(Types.VARCHAR);
		assertThat(result.hasValue(DETAIL)).isFalse();
	}
}
