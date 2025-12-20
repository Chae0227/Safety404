package com.webframework.safety404.service;

import com.webframework.safety404.domain.Category;
import com.webframework.safety404.domain.User;
import com.webframework.safety404.domain.Verification;
import com.webframework.safety404.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifyService {

    private final VerificationRepository verificationRepository;
    private final FileStorageService fileStorageService;

    // ===============================
    // 기본 조회
    // ===============================
    public Verification findById(Long id) {
        return verificationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("자료를 찾을 수 없습니다. id=" + id));
    }

    // ===============================
    // 🔥 목록 검색 (권한 반영)
    // ===============================
    public List<Verification> search(String category, String keyword, User loginUser) {

        if (loginUser == null) return List.of();

        if ("ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return search(category, keyword);
        }

        return searchMine(category, keyword, loginUser.getName());
    }

    public List<Verification> searchApproved(String category, String keyword, User loginUser) {

        if (loginUser == null) return List.of();

        if ("ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return searchApproved(category, keyword);
        }

        return searchApprovedMine(category, keyword, loginUser.getName());
    }

    // ===============================
    // 기존 검색 로직 (그대로)
    // ===============================
    public List<Verification> search(String category, String keyword) {

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (!hasCategory && !hasKeyword) {
            return verificationRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        }

        if (hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndCategoryOrderByCreatedAtDesc(
                            "PENDING",
                            Category.valueOf(category)
                    );
        }

        if (!hasCategory) {
            List<Verification> result = new ArrayList<>();
            result.addAll(
                    verificationRepository
                            .findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "PENDING", keyword
                            )
            );
            result.addAll(
                    verificationRepository
                            .findByStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "PENDING", keyword
                            )
            );
            return result;
        }

        List<Verification> result = new ArrayList<>();
        Category cat = Category.valueOf(category);

        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                "PENDING", cat, keyword
                        )
        );
        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                "PENDING", cat, keyword
                        )
        );

        return result;
    }

    public List<Verification> searchApproved(String category, String keyword) {

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (!hasCategory && !hasKeyword) {
            return verificationRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
        }

        if (hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndCategoryOrderByCreatedAtDesc(
                            "APPROVED",
                            Category.valueOf(category)
                    );
        }

        if (!hasCategory) {
            List<Verification> result = new ArrayList<>();
            result.addAll(
                    verificationRepository
                            .findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "APPROVED", keyword
                            )
            );
            result.addAll(
                    verificationRepository
                            .findByStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "APPROVED", keyword
                            )
            );
            return result;
        }

        List<Verification> result = new ArrayList<>();
        Category cat = Category.valueOf(category);

        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                "APPROVED", cat, keyword
                        )
        );
        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                "APPROVED", cat, keyword
                        )
        );

        return result;
    }

    // ===============================
    // 🔥 본인 글만 검색
    // ===============================
    private List<Verification> searchMine(String category, String keyword, String writer) {

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (!hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndWriterOrderByCreatedAtDesc("PENDING", writer);
        }

        if (hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndCategoryAndWriterOrderByCreatedAtDesc(
                            "PENDING", Category.valueOf(category), writer
                    );
        }

        if (!hasCategory) {
            List<Verification> result = new ArrayList<>();
            result.addAll(
                    verificationRepository
                            .findByStatusAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "PENDING", writer, keyword
                            )
            );
            result.addAll(
                    verificationRepository
                            .findByStatusAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "PENDING", writer, keyword
                            )
            );
            return result;
        }

        List<Verification> result = new ArrayList<>();
        Category cat = Category.valueOf(category);

        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                "PENDING", cat, writer, keyword
                        )
        );
        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                "PENDING", cat, writer, keyword
                        )
        );

        return result;
    }

    private List<Verification> searchApprovedMine(String category, String keyword, String writer) {

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (!hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndWriterOrderByCreatedAtDesc("APPROVED", writer);
        }

        if (hasCategory && !hasKeyword) {
            return verificationRepository
                    .findByStatusAndCategoryAndWriterOrderByCreatedAtDesc(
                            "APPROVED", Category.valueOf(category), writer
                    );
        }

        if (!hasCategory) {
            List<Verification> result = new ArrayList<>();
            result.addAll(
                    verificationRepository
                            .findByStatusAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "APPROVED", writer, keyword
                            )
            );
            result.addAll(
                    verificationRepository
                            .findByStatusAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                    "APPROVED", writer, keyword
                            )
            );
            return result;
        }

        List<Verification> result = new ArrayList<>();
        Category cat = Category.valueOf(category);

        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndWriterAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                                "APPROVED", cat, writer, keyword
                        )
        );
        result.addAll(
                verificationRepository
                        .findByStatusAndCategoryAndWriterAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                                "APPROVED", cat, writer, keyword
                        )
        );

        return result;
    }

    // ===============================
    // 저장 / 수정 / 상태 변경 (기존 그대로)
    // ===============================
    public Verification save(Verification v, MultipartFile file) throws IOException {

        if (v.getWriter() == null || v.getWriter().isBlank()) {
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

    public void update(Long id, Verification newData, MultipartFile file) throws IOException {
        Verification origin = findById(id);

        origin.setTitle(newData.getTitle());
        origin.setContent(newData.getContent());
        origin.setCategory(newData.getCategory());

        if (file != null && !file.isEmpty()) {
            String storedName = fileStorageService.storeFile(file);
            origin.setOriginalFilename(file.getOriginalFilename());
            origin.setStoredFilename(storedName);
            origin.setFileSize(file.getSize());
        }

        verificationRepository.save(origin);
    }

    public void delete(Long id) {
        verificationRepository.deleteById(id);
    }

    public void hide(Long id) {
        Verification v = findById(id);
        v.setStatus("HIDDEN");
        verificationRepository.save(v);
    }

    public void approve(Long id) {
        Verification v = findById(id);
        v.setStatus("APPROVED");
        verificationRepository.save(v);
    }
    
    public List<Verification> findRecentApproved(int limit) {
        return verificationRepository
                .findRecentByStatus("APPROVED", limit);
    }
}
