package se.sundsvall.vacationdocument.model;

import org.jilt.Builder;

@Builder(setterPrefix = "with", factoryMethod = "newDocument", toBuilder = "fromDocument")
public record Document(String documentId, DocumentStatus status) { }
