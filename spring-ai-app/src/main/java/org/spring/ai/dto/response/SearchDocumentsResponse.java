package org.spring.ai.dto.response;

import org.spring.ai.dto.DocumentInfo;

import java.util.List;

/**
 * 문서 검색 응답 DTO
 */
public record SearchDocumentsResponse(
   String query,
   int resultCount,
   List<DocumentInfo> documents
) {}
