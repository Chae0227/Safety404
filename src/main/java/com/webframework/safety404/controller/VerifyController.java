package com.webframework.safety404.controller;

import com.webframework.safety404.domain.User;
import com.webframework.safety404.domain.Verification;
import com.webframework.safety404.service.VerifyService;
import com.webframework.safety404.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {

    private final VerifyService verifyService;
    private final FileStorageService fileStorageService;

    /** 검증 요청 목록 (관리자: 전체 / 일반: 본인 글만) */
    @GetMapping
    public String list(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        List<Verification> posts = verifyService.search(category, keyword);

        if (loginUser == null) {
            posts = List.of();
        } else if (!"ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            String myName = loginUser.getName();
            posts = posts.stream()
                    .filter(v -> myName.equals(v.getWriter()))
                    .toList();
        }

        model.addAttribute("posts", posts);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "verify/list";
    }

    /** 승인된 자료실 (전원 공개) */
    @GetMapping("/approved")
    public String approved(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {
        model.addAttribute("posts",
                verifyService.searchApproved(category, keyword));
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "verify/approved";
    }

    /** 등록 폼 */
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        model.addAttribute("post", new Verification());
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("writerName",
                loginUser != null ? loginUser.getName() : "비회원");
        return "verify/form";
    }

    /** 등록 */
    @PostMapping
    public String create(
            @ModelAttribute Verification v,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session
    ) throws Exception {

        User loginUser = (User) session.getAttribute("loginUser");
        v.setWriter(loginUser != null ? loginUser.getName() : "비회원");
        verifyService.save(v, file);
        return "redirect:/verify";
    }

    /** 상세 (전원 접근 가능) */
    @GetMapping("/{id}")
    public String detail(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session
    ) {
        Verification post = verifyService.findById(id);
        model.addAttribute("post", post);

        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = false;
        boolean isAdmin = false;

        if (loginUser != null) {
            mine = loginUser.getName().equals(post.getWriter());
            isAdmin = "ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole());
        }

        model.addAttribute("mine", mine);
        model.addAttribute("isAdmin", isAdmin);
        return "verify/detail";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session
    ) {
        Verification post = verifyService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null ||
                !loginUser.getName().equals(post.getWriter())) {
            return "redirect:/verify";
        }

        model.addAttribute("post", post);
        return "verify/form";
    }

    /** 수정 */
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable("id") Long id,
            @ModelAttribute Verification v,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session
    ) throws Exception {

        Verification post = verifyService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null ||
                !loginUser.getName().equals(post.getWriter())) {
            return "redirect:/verify";
        }

        verifyService.update(id, v, file);
        return "redirect:/verify/" + id;
    }

    /** 🔥 승인 (관리자만) */
    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null ||
                !"ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return "redirect:/verify";
        }

        verifyService.approve(id);
        return "redirect:/verify/approved";
    }

    /** 🔥 가리기 (관리자만) */
    @PostMapping("/{id}/hide")
    public String hide(
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null ||
                !"ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return "redirect:/verify";
        }

        verifyService.hide(id);
        return "redirect:/verify/" + id;
    }

    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        Verification post = verifyService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = loginUser != null &&
                loginUser.getName().equals(post.getWriter());
        boolean isAdmin = loginUser != null &&
                "ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole());

        if (!mine && !isAdmin) return "redirect:/verify";

        verifyService.delete(id);
        return "redirect:/verify";
    }

    /** 파일 다운로드 */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable("id") Long id
    ) throws Exception {

        Verification post = verifyService.findById(id);
        Resource resource =
                fileStorageService.loadFileAsResource(post.getStoredFilename());

        String encodedName = URLEncoder.encode(
                post.getOriginalFilename(), StandardCharsets.UTF_8
        ).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"")
                .body(resource);
    }
}
