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

@Controller
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {

    private final VerifyService verifyService;
    private final FileStorageService fileStorageService;

    /** 대기 목록 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", verifyService.findAllPending());
        return "verify/list";
    }

    /** 승인된 목록 */
    @GetMapping("/approved")
    public String approved(Model model) {
        model.addAttribute("posts", verifyService.findAllApproved());
        return "verify/approved";
    }

    /** 자료 등록 폼 */
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        model.addAttribute("post", new Verification());

        User loginUser = (User) session.getAttribute("loginUser");

        String writerName = (loginUser != null && loginUser.getName() != null)
                ? loginUser.getName()
                : "비회원";

        model.addAttribute("writerName", writerName);

        return "verify/form";
    }

    /** 등록 처리 */
    @PostMapping
    public String create(
            @ModelAttribute Verification v,
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) throws Exception {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser != null) {
            v.setWriter(loginUser.getName());
        } else {
            v.setWriter("비회원");
        }

        verifyService.save(v, file);
        return "redirect:/verify";
    }

    /** 상세 페이지 */
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
        String writerUsername = null;

        if (loginUser != null) {

            // 본인 글인지 확인 (writer = name)
            if (post.getWriter() != null &&
                    loginUser.getName() != null &&
                    loginUser.getName().equals(post.getWriter())) {
                mine = true;
            }

            // 관리자 여부 체크
            if ("ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole())) {
                isAdmin = true;
            }

            writerUsername = loginUser.getUsername();
        }

        model.addAttribute("mine", mine);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("writerUsername", writerUsername);

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
                loginUser.getName() == null ||
                post.getWriter() == null ||
                !loginUser.getName().equals(post.getWriter())) {

            return "redirect:/verify";
        }

        model.addAttribute("post", post);
        return "verify/form";
    }

    /** 수정 처리 */
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
                loginUser.getName() == null ||
                post.getWriter() == null ||
                !loginUser.getName().equals(post.getWriter())) {
            return "redirect:/verify";
        }

        verifyService.update(id, v, file);
        return "redirect:/verify/" + id;
    }

    /** 삭제 (본인 or 관리자) */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        Verification post = verifyService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = false;
        boolean isAdmin = false;

        if (loginUser != null) {
            mine = loginUser.getName().equals(post.getWriter());
            isAdmin = "ROLE_ADMIN".equalsIgnoreCase(loginUser.getRole());
        }

        if (!mine && !isAdmin) {
            return "redirect:/verify";
        }

        verifyService.delete(id);
        return "redirect:/verify";
    }

    /** 가리기 (관리자) */
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

    /** 승인 (관리자) */
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

    /** 파일 다운로드 */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable("id") Long id
    ) throws Exception {

        Verification post = verifyService.findById(id);

        if (post.getStoredFilename() == null) {
            throw new IllegalArgumentException("첨부파일이 존재하지 않습니다.");
        }

        Resource resource = fileStorageService.loadFileAsResource(post.getStoredFilename());

        String encodedName = URLEncoder.encode(
                post.getOriginalFilename(),
                StandardCharsets.UTF_8
        ).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"")
                .body(resource);
    }
}
