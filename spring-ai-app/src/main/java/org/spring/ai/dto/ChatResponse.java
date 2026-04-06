package org.spring.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "채팅 응답")
@Builder
public record ChatResponse(
    @Schema(description = "AI의 응답 메시지")
    String message,
    @Schema(description = "대화 ID")
    String conversationId,
    @Schema(description = "응답 생성 시간")
    LocalDateTime timestamp,
    @Schema(description = "사용된 토큰 수")
    TokenUsage tokenUsage
) {
    public static ChatResponse of(String message, String conversationId, LocalDateTime timestamp, TokenUsage tokenUsage) {
        return ChatResponse.builder()
                .message(message)
                .conversationId(conversationId)
                .timestamp(timestamp)
                .tokenUsage(tokenUsage)
                .build();
    }

    public static ChatResponse of(String message, String conversationId, TokenUsage tokenUsage) {
        return ChatResponse.of(message, conversationId, LocalDateTime.now(), tokenUsage);
    }

    public static ChatResponse of(String message, String conversationId) {
        return ChatResponse.of(message, conversationId, LocalDateTime.now(), null);
    }
}
