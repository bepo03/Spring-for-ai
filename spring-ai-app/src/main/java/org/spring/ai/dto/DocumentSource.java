package org.spring.ai.dto;

public record DocumentSource(
        String filename,
        String documentId,
        String preview
) {}
