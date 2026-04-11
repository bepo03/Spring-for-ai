package org.spring.ai.dto.response;

import org.spring.ai.dto.DocumentSource;

import java.util.List;

public record RagResponse(
   String answer,
   List<DocumentSource> sources
) {}
