package org.spring.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.spring.ai.dto.request.ChatRequest;
import org.spring.ai.dto.response.ChatResponse;
import org.spring.ai.service.OllamaChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "OLLAMA Chat", description = "로컬 AI 모델 챗봇 API")
@RestController
@RequestMapping("/api/ollama")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OllamaChatController {
    private final OllamaChatService ollamaChatService;

    @Operation(summary = "간단한 채팅", description = "대화 이력을 사용하지 않고 단일 메시지로 AI와 대화합니다.")
    @PostMapping("/simple")
    public ResponseEntity<ChatResponse> simpleChat(
            @RequestBody
            Map<String, String> request
    ) {
        String message = request.get("message");
        ChatResponse response = ollamaChatService.chat(message);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이력 기반 채팅", description = "대화 이력을 사용하여 AI와 대화합니다. conversationId를 통해 대화를 이어갈 수 있습니다.")
    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @RequestBody
            ChatRequest request
    ) {
        ChatResponse response = ollamaChatService.chatWithHistory(
                request.message(),
                request.conversationId()
        );
        return ResponseEntity.ok(response);
    }
}
