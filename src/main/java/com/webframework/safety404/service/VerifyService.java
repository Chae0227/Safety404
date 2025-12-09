package com.webframework.safety404.service;

import com.webframework.safety404.domain.Verification;
import com.webframework.safety404.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifyService {

    private final VerificationRepository verificationRepository;
    private final FileStorageService fileStorageService;

    // 대기 목록
    public List<Verification> findAllPending() {
        return verificationRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // 승인 목록
    public List<Verification> findAllApproved() {
        return verificationRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
    }

    // 상세
    public Verification findById(Long id) {
        return verificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자료를 찾을 수 없습니다. id=" + id));
    }

    // 저장
    public Verification save(Verification v, MultipartFile file) throws IOException {

        if (v.getWriter() == null || v.getWriter().trim().isEmpty()) {
            v.setWriter("비회원");
        }

        if (file != null && !file.isEmpty()) {
            String storedName = fileStorageService.storeFile(file);
            v.setOriginalFilename(file.getOriginalFilename());
            v.setStoredFilename(storedName);
            v.setFileSize(file.getSize());
        }

        v.setStatus("PENDING");
        return verificationRepository.save(v);
    }

    // 수정
    public void update(Long id, Verification newData, MultipartFile file) throws IOException {

        Verification origin = findById(id);

        origin.setTitle(newData.getTitle());
        origin.setContent(newData.getContent());

        // 파일 변경 시에만 새로 저장
        if (file != null && !file.isEmpty()) {
            String storedName = fileStorageService.storeFile(file);
            origin.setOriginalFilename(file.getOriginalFilename());
            origin.setStoredFilename(storedName);
            origin.setFileSize(file.getSize());
        }

        verificationRepository.save(origin);
    }

    // 삭제
    public void delete(Long id) {
        verificationRepository.deleteById(id);
    }

    // 가리기 (HIDDEN 처리)
    public void hide(Long id) {
        Verification v = findById(id);
        v.setStatus("HIDDEN");
        verificationRepository.save(v);
    }

    // 승인
    public void approve(Long id) {
        Verification v = findById(id);
        v.setStatus("APPROVED");
        verificationRepository.save(v);
    }
}
