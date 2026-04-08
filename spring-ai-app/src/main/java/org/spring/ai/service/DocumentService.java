package org.spring.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.ai.entity.DocumentEntity;
import org.spring.ai.repository.DocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    /**
     * 문서 업로드 및 벡터화 처리
     */
    @Transactional
    public DocumentEntity uploadDocument(MultipartFile file) throws IOException {
        log.info("문서 업로드 시작: {}", file.getOriginalFilename());

        // 1. 파일 내용 읽기 (UTF-8로 인코딩)
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        // 2. DB에 원본 문서 저장
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(UUID.randomUUID().toString())
                .filename(file.getOriginalFilename())
                .content(content)
                .contentType(file.getContentType())
                .build();

        // 3. 문서 분할 (Chunking)
        List<Document> chunks = splitDocument(
                content,
                documentEntity.getId(),
                file.getOriginalFilename());
        documentEntity.setChunkCount(chunks.size());
        documentRepository.save(documentEntity);

        // 4. 벡터 저장소에 저장 (자동으로 임베딩 생성)
        vectorStore.add(chunks);

        log.info("문서 업로드 완료: {}, 청크 수: {}", file.getOriginalFilename(), chunks.size());
        return documentEntity;
    }

    /**
     * 문서 분할 (Chunking)
     */
    private List<Document> splitDocument(String content, String documentId, String filename) {
        // TokenTextSplitter: 토큰 기반으로 문서 분할
        TextSplitter splitter = new TokenTextSplitter(
                500, // defaultChunkSize: 기본 청크 크기
                100, // minChunkSizeChars: 최소 청크 크기 (문자 수)
                5,   // minChunkLengthToEmbed: 임베딩할 최소 청크 길이
                1,   // maxNumChunks: 최대 청크 수
                true // keepSeparator: 구분자 유지 여부
        );

        // 메타데이터 추가
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("document_id", documentId);
        metadata.put("filename", filename);
        metadata.put("source", "user_upload");

        // Document 객체 생성
        Document document = new Document(content, metadata);

        // 문서 분할 실행
        return splitter.split(document);
    }

    /**
     * TextReader를 사용하여 문서 분할 (대안)
     */
    private List<Document> splitDocumentWithReader(MultipartFile file, String documentId) throws IOException {
        // ByteArrayResource로 변환
        Resource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // TextReader 사용
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("document_id", documentId);
        textReader.getCustomMetadata().put("filename", file.getOriginalFilename());
        textReader.getCustomMetadata().put("source", "user_upload");

        List<Document> documents = textReader.get();

        // TokenTextSplitter로 분할
        TextSplitter splitter = new TokenTextSplitter();
        return splitter.split(documents);
    }


    /**
     * 텍스트로 직접 문서 추가 (파일 업로드 없이)
     */
    @Transactional
    public DocumentEntity addTextDocument(String filename, String content) {
        log.info("텍스트 문서 추가: {}", filename);

        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(UUID.randomUUID().toString())
                .filename(filename)
                .content(content)
                .contentType("text/plain")
                .build();

        List<Document> chunks = splitDocument(content, documentEntity.getId(), filename);
        documentEntity.setChunkCount(chunks.size());
        documentRepository.save(documentEntity);

        vectorStore.add(chunks);

        return documentEntity;
    }

    /**
     * 모든 문서 조회
     */
    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * 문서 삭제
     */
    @Transactional
    public void deleteDocument(String documentId) {
        log.info("문서 삭제: {}", documentId);

        // DB에서 삭제
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다: " + documentId));

        documentRepository.deleteById(documentId);

        // 벡터 저장소에서 해당 문서의 청크도 삭제하는 로직
        try {
            // document_id 메타데이터를 기준으로 벡터 삭제
            List<String> documentIds = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("")
                            .filterExpression("document_id == '" + documentId + "'")
                            .topK(1000)
                            .build()
                    ).stream()
                    .map(document -> document.getId())
                    .toList();

            if (!documentIds.isEmpty()) {
                vectorStore.delete(documentIds);
                log.info("벡터 저장소에서 {} 문서 청크 삭제 완료", documentIds.size());
            }
        } catch (Exception e) {
            log.error("벡터 저장소에서 문서 청크 삭제 중 오류 발생: {}", e.getMessage());
            // 예외 처리: 벡터 삭제 실패 시에도 DB 삭제는 유지
        }
    }

    /**
     * 문서 내에서 유사도 검색
     */
    public List<Document> searchInDocument(String documentId, String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .filterExpression("document_id == '" + documentId + "'")
                        .topK(topK)
                        .build()
        );
    }
}
