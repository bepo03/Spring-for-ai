package org.spring.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient.Builder chatClientBuilder;

    public String chat(String message) {
        ChatClient chatClient = chatClientBuilder.build();

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String chatWithContext(String userMessage) {
        ChatClient chatClient = chatClientBuilder.build();

        return chatClient.prompt()
                .system("당신은 친절한 AI 어시스턴트입니다. 항상 한국어로 답변하세요.")
                .user(userMessage)
                .call()
                .content();
    }
}
