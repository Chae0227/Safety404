package com.webframework.safety404.repository;

import com.webframework.safety404.domain.Category;
import com.webframework.safety404.domain.Verification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // ===============================
    // 기본 조회
    // ===============================
    List<Verification> findByStatusOrderByCreatedAtDesc(String status);

    List<Verification> findByStatusAndCategoryOrderByCreatedAtDesc(
            String status, Category category
    );

    List<Verification> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, String title
    );

    List<Verification> findByStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, String content
    );

    List<Verification> findByStatusAndCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, Category category, String title
    );

    List<Verification> findByStatusAndCategoryAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, Category category, String content
    );

    // ===============================
    // 🔥 writer 조건
    // ===============================
    List<Verification> findByStatusAndWriterOrderByCreatedAtDesc(
            String status, String writer
    );

    List<Verification> findByStatusAndCategoryAndWriterOrderByCreatedAtDesc(
            String status, Category category, String writer
    );

    List<Verification> findByStatusAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, String writer, String title
    );

    List<Verification> findByStatusAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, String writer, String content
    );

    List<Verification> findByStatusAndCategoryAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, Category category, String writer, String title
    );

    List<Verification> findByStatusAndCategoryAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String status, Category category, String writer, String content
    );

    // ===============================
    // 🔥 메인 페이지용 최근 승인 글
    // ===============================
    @Query("""
        select v
        from Verification v
        where v.status = :status
        order by v.createdAt desc
    """)
    List<Verification> findRecentByStatus(
            @Param("status") String status,
            Pageable pageable
    );

    default List<Verification> findRecentByStatus(String status, int limit) {
        return findRecentByStatus(status, Pageable.ofSize(limit));
    }
}
