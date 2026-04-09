package org.spring.ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.ai.dto.DocumentInfo;
import org.spring.ai.dto.request.QuestionRequest;
import org.spring.ai.dto.response.AnswerResponse;
import org.spring.ai.dto.response.SearchDocumentsResponse;
import org.spring.ai.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {
    private final RagService ragService;

    /**
     * 기본 RAG 질문 처리 엔드포인트
     */
    @PostMapping("/ask")
    public ResponseEntity<AnswerResponse> ask(
            @RequestBody
            QuestionRequest request
    ) {
        try {
            log.info("RAG 질문 요청: {}", request.question());

            String answer = ragService.ask(request.question());

            return ResponseEntity.ok(new AnswerResponse(answer, "success"));
        } catch (Exception e) {
            log.error("RAG 질문 처리 실패: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnswerResponse(
                            "죄송합니다. 질문 처리 중 오류가 발생했습니다." + e.getMessage(),
                            "error"));
        }
    }

    /**
     * Advisor RAG 질문 처리 엔드포인트
     */
    @PostMapping("/ask/advisor")
    public ResponseEntity<AnswerResponse> askWithAdvisor(
            @RequestBody
            QuestionRequest request
    ) {
        try {
            log.info("RAG 질문 요청 (Advisor): {}", request.question());

            String answer = ragService.askWithAdvisor(request.question());

            return ResponseEntity.ok(new AnswerResponse(answer, "success"));
        } catch (Exception e) {
            log.error("RAG 질문 처리 실패 (Advisor): {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnswerResponse(
                            "죄송합니다. Advisor 질문 처리 중 오류가 발생했습니다." + e.getMessage(),
                            "error"));
        }
    }

    /**
     * 문서 검색 엔드포인트 (답변 생성 없이)
     */
    @GetMapping("/search")
    public ResponseEntity<SearchDocumentsResponse> searchDocuments(
            @RequestParam
            String query,
            @RequestParam(defaultValue = "5")
            int topK
    ) {
        try {
            log.info("문서 검색 요청: query={}, topK={}", query, topK);

            List<Document> documents = ragService.searchDocuments(query, topK);

            List<DocumentInfo> documentInfos = documents.stream()
                    .map(document -> new DocumentInfo(
                            document.getId(),
                            document.getText(),
                            document.getMetadata()
                    ))
                    .toList();

            return ResponseEntity.ok(new SearchDocumentsResponse(
                    query,
                    documentInfos.size(),
                    documentInfos
            ));
        } catch (Exception e) {
            log.error("문서 검색 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SearchDocumentsResponse(
                            query,
                            0,
                            List.of()
                    ));
        }
    }

    /**
     * 유사도 점수와 함께 문서 검색 엔드포인트
     */
    @GetMapping("/search-with-score")
    public ResponseEntity<SearchDocumentsResponse> searchWithScore(
            @RequestParam
            String query,
            @RequestParam(defaultValue = "5")
            int topK,
            @RequestParam(defaultValue = "0.7")
            double threshold
    ) {
        try {
            log.info("문서 검색 요청 (score 포함): query={}, topK={}, threshold={}", query, topK, threshold);

            List<Document> documents = ragService.searchWithScore(query, topK, threshold);

            List<DocumentInfo> documentInfos = documents.stream()
                    .map(document -> new DocumentInfo(
                            document.getId(),
                            document.getText(),
                            document.getMetadata()
                    ))
                    .toList();

            return ResponseEntity.ok(new SearchDocumentsResponse(
                    query,
                    documentInfos.size(),
                    documentInfos
            ));
        } catch (Exception e) {
            log.error("문서 검색 실패 (score 포함): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SearchDocumentsResponse(
                            query,
                            0,
                            List.of()
                    ));
        }
    }
}
