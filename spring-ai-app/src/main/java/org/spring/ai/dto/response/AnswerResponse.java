package org.spring.ai.dto.response;

/**
 * 답변 응답 DTO
 */
public record AnswerResponse(
        String answer,
        String status
) {}
