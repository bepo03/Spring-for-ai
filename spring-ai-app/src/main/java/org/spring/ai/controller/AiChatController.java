package org.spring.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI Chat", description = "AI 챗봇 API")
@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final ChatClient chatClient;

    public AiChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Operation(summary = "기본 채팅", description = "AI와 기본 대화를 수행합니다.")
    @PostMapping("/chat")
    public String chat(
            @RequestBody
            String message
    ) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
