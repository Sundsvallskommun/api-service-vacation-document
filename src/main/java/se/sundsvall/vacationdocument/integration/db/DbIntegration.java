package se.sundsvall.vacationdocument.integration.db;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.sql.Types;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import se.sundsvall.vacationdocument.model.DocumentStatus;

@Component
public class DbIntegration {

	static final String DOCUMENT_ID = "document_id";
	static final String MUNICIPALITY_ID = "municipality_id";
	static final String STATUS = "status";
	static final String DETAIL = "detail";

	static final String SQL_EXISTS_BY_ID = "SELECT COUNT(d.document_id) FROM document d WHERE d.document_id = :document_id AND d.municipality_id = :municipality_id";
	static final String SQL_INSERT = "INSERT INTO document (document_id, municipality_id, status) VALUES (:document_id, :municipality_id, :status)";
	static final String SQL_UPDATE = "UPDATE document d SET d.status = :status WHERE d.document_id = :document_id AND d.municipality_id = :municipality_id";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	DbIntegration(final NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean existsById(final String documentId, final String municipalityId) {
		var count = jdbcTemplate.queryForObject(SQL_EXISTS_BY_ID, Map.of(DOCUMENT_ID, documentId, MUNICIPALITY_ID, municipalityId), Long.class);

		if (count == null) {
			throw new DataRetrievalFailureException("Unable to check if document exists");
		}

		return count > 0;
	}

	public void saveDocument(final String documentId, final String municipalityId, final DocumentStatus status) {
		jdbcTemplate.update(SQL_INSERT, mapToSqlParameterSource(documentId, municipalityId, status, empty()));
	}

	public void updateDocument(final String documentId, final String municipalityId, final DocumentStatus status) {
		updateDocument(documentId, municipalityId, status, null);
	}

	public void updateDocument(final String documentId, final String municipalityId, final DocumentStatus status, final String detail) {
		jdbcTemplate.update(SQL_UPDATE, mapToSqlParameterSource(documentId, municipalityId, status, ofNullable(detail)));
	}

	SqlParameterSource mapToSqlParameterSource(final String documentId, final String municipalityId,
		final DocumentStatus status, final Optional<String> detail) {
		var parameters = new MapSqlParameterSource()
			.addValue(DOCUMENT_ID, documentId)
			.addValue(MUNICIPALITY_ID, municipalityId)
			.addValue(STATUS, status, Types.VARCHAR);

		detail.ifPresent(value -> parameters.addValue(DETAIL, value));

		return parameters;
	}
}
