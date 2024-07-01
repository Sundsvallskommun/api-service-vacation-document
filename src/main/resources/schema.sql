CREATE TABLE document (document_id VARCHAR(16) NOT NULL, status ENUM ('NEW', 'NOT_APPROVED', 'PROCESSING', 'DONE','ERROR'), PRIMARY KEY (document_id))
