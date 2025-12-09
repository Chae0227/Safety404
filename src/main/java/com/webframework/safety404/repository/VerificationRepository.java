package com.webframework.safety404.repository;

import com.webframework.safety404.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // 상태값으로 조회 (PENDING / APPROVED)
    List<Verification> findByStatusOrderByCreatedAtDesc(String status);
}
