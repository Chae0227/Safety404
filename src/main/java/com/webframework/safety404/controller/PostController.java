package com.webframework.safety404.controller;

import com.webframework.safety404.domain.Post;
import com.webframework.safety404.domain.User;
import com.webframework.safety404.service.PostService;
import com.webframework.safety404.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // 목록 + 검색
    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "type", required = false) String type,
                       Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("posts", postService.search(type, keyword));
        } else {
            model.addAttribute("posts", postService.findAll());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "posts/list";
    }

    // 새 글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model, HttpSession session) {

        Post post = new Post();
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser != null) {
            post.setWriter(loginUser.getName());
            post.setWriterId(loginUser.getId());
        }

        model.addAttribute("post", post);
        return "posts/form";
    }

    // 저장
    @PostMapping
    public String create(@ModelAttribute Post post,
                         @RequestParam(value = "anonymous", required = false) String anonymous,
                         @RequestParam(value = "pinned", required = false) String pinned,
                         HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        post.setPinned(pinned != null);

        if (anonymous != null) { // 익명
            post.setWriter("익명");
            post.setWriterId(null);
        }
        else if (loginUser != null) {
            post.setWriter(loginUser.getName());
            post.setWriterId(loginUser.getId());
        }
        else {
            post.setWriter("비회원");
            post.setWriterId(null);
        }

        postService.save(post);
        return "redirect:/posts";
    }

    // 상세보기
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {

        Post post = postService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = (loginUser != null && post.getWriterId() != null
                        && loginUser.getId().equals(post.getWriterId()));

        boolean isAdmin = (loginUser != null && "ROLE_ADMIN".equals(loginUser.getRole()));

        // ★ 작성자 username 찾기
        String writerUsername = null;
        if (post.getWriterId() != null) {
            User writer = userService.findById(post.getWriterId()); // ← 오타 수정됨
            if (writer != null) writerUsername = writer.getUsername();
        }

        model.addAttribute("post", post);
        model.addAttribute("mine", mine);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("writerUsername", writerUsername);

        return "posts/detail";
    }


    // 수정 폼 (본인 + 관리자 가능)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {

        Post post = postService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = (loginUser != null && post.getWriterId() != null &&
                        loginUser.getId().equals(post.getWriterId()));

        boolean isAdmin = (loginUser != null && "ROLE_ADMIN".equals(loginUser.getRole()));

        if (!mine && !isAdmin) {
            return "redirect:/posts?error=forbidden";
        }

        model.addAttribute("post", post);
        return "posts/form";
    }

    // 수정 처리
    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute Post formPost,
                         @RequestParam(value = "anonymous", required = false) String anonymous,
                         @RequestParam(value = "pinned", required = false) String pinned,
                         HttpSession session) {

        Post post = postService.findById(id);
        User loginUser = (User) session.getAttribute("loginUser");

        boolean mine = (loginUser != null && post.getWriterId() != null &&
                        loginUser.getId().equals(post.getWriterId()));

        boolean isAdmin = (loginUser != null && "ROLE_ADMIN".equals(loginUser.getRole()));

        if (!mine && !isAdmin) {
            return "redirect:/posts?error=forbidden";
        }

        post.setTitle(formPost.getTitle());
        post.setContent(formPost.getContent());
        post.setPinned(pinned != null);

        if (anonymous != null) {
            post.setWriter("익명");
            post.setWriterId(null);
        } else {
            post.setWriter(loginUser.getName());
            post.setWriterId(loginUser.getId());
        }

        postService.save(post);
        return "redirect:/posts/" + id;
    }

    // 게시글 숨기기 (관리자 전용)
    @PostMapping("/{id}/hide")
    public String hide(@PathVariable("id") Long id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !"ROLE_ADMIN".equals(loginUser.getRole())) {
            return "redirect:/posts?error=forbidden";
        }

        Post post = postService.findById(id);

        post.setHidden(true);
        post.setTitle("관리자가 게시물을 가렸습니다.");
        post.setContent("게시글 내용이 운영정책을 위반하여 숨김 처리되었습니다.");

        postService.save(post);

        return "redirect:/posts/" + id;
    }

    // 삭제 (본인 + 관리자)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        Post post = postService.findById(id);

        boolean mine = (loginUser != null && post.getWriterId() != null &&
                        loginUser.getId().equals(post.getWriterId()));

        boolean isAdmin = (loginUser != null && "ROLE_ADMIN".equals(loginUser.getRole()));

        if (!mine && !isAdmin) {
            return "redirect:/posts?error=forbidden";
        }

        postService.deleteById(id);
        return "redirect:/posts";
    }
}
