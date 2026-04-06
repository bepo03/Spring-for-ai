package org.spring.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.spring.ai.dto.ChatResponse;
import org.spring.ai.dto.TokenUsage;
import org.spring.ai.global.custom.ContextLengthExceededException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OllamaChatService implements ChatService {
    private final ChatClient ollamaChatClient;
    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();

    public OllamaChatService(ChatClient.Builder chatClientBuilder) {
        this.ollamaChatClient = chatClientBuilder.build();
    }

    @Override
    public ChatResponse chat(String question) {
        String response = prompt(question);
        return ChatResponse.of(response, UUID.randomUUID().toString());
    }

    @Override
    public ChatResponse chatWithHistory(String question, String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }

        List<Message> history = conversations.getOrDefault(conversationId, new ArrayList<>());

        UserMessage userMessage = new UserMessage(question);
        history.add(userMessage);
        org.springframework.ai.chat.model.ChatResponse response = promptWithHistory(question, history);

        String assistantResponse = response.getResult().getOutput().getText();
        if (assistantResponse == null || assistantResponse.isBlank()) {
            throw new IllegalStateException("AI 응답이 비어 있습니다.");
        }

        AssistantMessage assistantMessage = new AssistantMessage(assistantResponse);
        history.add(assistantMessage);
        conversations.put(conversationId, history);

        var metadata = response.getMetadata();
        TokenUsage tokenUsage = null;
        if (metadata != null && metadata.getUsage() != null) {
            var usage = metadata.getUsage();
            tokenUsage = new TokenUsage(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        }

        return ChatResponse.of(assistantResponse, conversationId, tokenUsage);
    }

    @Override
    public Flux<String> chatStream(String question) {
        return promptStream(question);
    }

    @Override
    public List<Message> getConversationHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, Collections.emptyList());
    }

    @Override
    public void clearConversationBy(String conversationId) {
        conversations.remove(conversationId);
    }

    @Override
    public void clearAllConversations() {
        conversations.clear();
    }

    private String prompt(String question) {
        return ollamaChatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    private org.springframework.ai.chat.model.ChatResponse promptWithHistory(String question, List<Message> history) {
        try {
            return ollamaChatClient.prompt()
                    .messages(history)
                    .user(question)
                    .call()
                    .chatResponse();
        } catch (ContextLengthExceededException e) {
            throw new ContextLengthExceededException(e.getMessage());
        }
    }

    private Flux<String> promptStream(String question) {
        return ollamaChatClient.prompt()
                .user(question)
                .stream()
                .content();
    }
}
