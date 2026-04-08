package org.spring.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;


    /**
     * RAG 질문 처리 메서드
     */
    public String ask(String question) {
        log.info("RAG 질문: {}", question);

        // 1. 벡터 저장소에서 유사한 문서 검색
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build()
        );

        if (relevantDocs.isEmpty()) {
            log.warn("관련 문서가 없습니다.");
            return "죄송합니다. 관련된 정보를 찾을 수 없습니다. 다른 질문을 해보시겠어요?";
        }

        log.info("관련 문서 {}개 검색됨.", relevantDocs.size());

        // 2. 검색된 문서를 Context로 조합
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        // 3. LLM에 질문과 Context를 함께 전달하여 답변 생성
        String prompt = String.format("""
                다음 문서들을 참고하여 질문에 답변해주세요.
                문서에 없는 내용은 답변하지 마세요.
                
                [참고 문서]
                %s
                
                [질문]
                %s
                
                [답변]
                """, context, question);

        ChatClient chatClient = chatClientBuilder.build();
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("RAG 답변 생성 완료");
        return answer;
    }

    /**
     * RAG 질문 처리 메서드 QuestionAnswerAdvisor (Spring AI 고급 기능)
     */
    public String askWithAdvisor(String question) {
        log.info("RAG 질문 (Advisor): {}", question);

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();

        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return answer;
    }

    /**
     * 단순 문서 검색 메서드 (RAG 없이 벡터 검색만)
     */
    public List<Document> searchDocuments(String query, int topK) {
        log.info("문서 검색: {}, topK={}", query, topK);
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build());
    }

    /**
     * 문서 검색 메서드 (score 포함)
     */
    public List<Document> searchWithScore(String query, int topK, double threshold) {
        log.info("문서 검색 (score 포함): {}, topK={}, threshold={}", query, topK, threshold);
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(threshold)
                        .build());
    }
}
