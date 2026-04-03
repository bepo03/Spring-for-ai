package org.spring.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.spring.ai.dto.ChatRequest;
import org.spring.ai.dto.ChatResponse;
import org.spring.ai.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI Chat", description = "AI 챗봇 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {
    private final ChatService chatService;

    @Operation(summary = "기본 채팅", description = "AI와 기본 대화를 수행합니다.")
    @PostMapping("/chat")
    public ChatResponse chat(
            @RequestBody
            ChatRequest request
    ) {
        String response = chatService.chat(request.message());
        return new ChatResponse(response);
    }

    @Operation(summary = "컨텍스트 기반 채팅", description = "System Message를 활용한 대화")
    @PostMapping("/chat/context")
    public ChatResponse chatWithContext(
            @RequestBody
            ChatRequest request
    ) {
        String response = chatService.chatWithContext(request.message());
        return new ChatResponse(response);
    }
}
