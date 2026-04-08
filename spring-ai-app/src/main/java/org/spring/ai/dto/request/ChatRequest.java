package org.spring.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 요청")
public record ChatRequest(
    @Schema(description = "사용할 AI 모델")
    String model,
    @Schema(description = "사용자 메시지", example = "안녕하세요!")
    String message,
    @Schema(description = "대화 ID (선택 사항, 대화를 이어갈 때 사용)")
    String conversationId
) {
    public ChatRequest {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지는 필수입니다.");
        }
    }
}
