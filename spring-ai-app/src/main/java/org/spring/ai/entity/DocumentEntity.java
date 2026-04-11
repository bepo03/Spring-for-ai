package org.spring.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String contentType; // PDF, DOCX, TXT, etc.

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private Integer chunkCount; // 이 문서가 몇 개의 청크로 나뉘었는지 저장

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON 형태의 메타데이터
}
