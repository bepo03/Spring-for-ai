package org.spring.ai.service;

import org.spring.ai.dto.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    ChatResponse chat(String question);

    ChatResponse chatWithHistory(String question, String conversationId);

    Flux<String> chatStream(String question);

    List<Message> getConversationHistory(String conversationId);

    void clearConversationBy(String conversationId);

    void clearAllConversations();
}
