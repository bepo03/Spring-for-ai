package org.spring.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 사용량 정보")
public record TokenUsage(
    @Schema(description = "프롬프트에 사용된 토큰 수")
    Integer promptTokens,
    @Schema(description = "응답에 사용된 토큰 수")
    Integer completionTokens,
    @Schema(description = "총 사용된 토큰 수")
    Integer totalTokens
) {}
