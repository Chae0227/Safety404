package com.webframework.safety404.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;   // 비회원 가능
    
    @Column(nullable = false)
    private boolean pinned = false;
    
    // ✅ 관리자에 의해 숨김 처리 여부
    @Column(nullable = false)
    private boolean hidden = false;
    
    @Column(name = "writer_id")
    private Long writerId;   // 작성자 User PK

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 저장 직전 자동 실행
    @PrePersist
    public void prePersist() {
        if (this.writer == null || this.writer.trim().isEmpty()) {
            this.writer = "비회원";
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 수정 직전 자동 실행
    @PreUpdate
    public void preUpdate() {
        if (this.writer == null || this.writer.trim().isEmpty()) {
            this.writer = "비회원";
        }
        this.updatedAt = LocalDateTime.now();
    }
}
