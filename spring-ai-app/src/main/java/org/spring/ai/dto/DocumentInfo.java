package org.spring.ai.dto;

import java.util.Map;

/**
 * 문서 정보 DTO
 */
public record DocumentInfo(
   String id,
   String content,
   Map<String, Object> metadata
) {}
