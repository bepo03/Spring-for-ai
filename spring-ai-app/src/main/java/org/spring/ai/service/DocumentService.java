package org.spring.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.ai.entity.DocumentEntity;
import org.spring.ai.repository.DocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
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
                5, // minChunkLengthToEmbed: 임베딩할 최소 청크 길이
                1, // maxNumChunks: 최대 청크 수
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
}
