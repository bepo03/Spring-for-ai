package org.spring.ai.dto.response;

/**
 * 검색 요약 응답 DTO
 */
public record SearchSummaryResponse(
   String query,
   String summary
) {}
