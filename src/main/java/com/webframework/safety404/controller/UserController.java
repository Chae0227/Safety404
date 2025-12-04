package com.webframework.safety404.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.webframework.safety404.domain.User;
import com.webframework.safety404.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    // 회원가입 폼
    @GetMapping("/signup")
    public String signupForm() {
        return "user/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(User user, Model model) {

        if (service.existsUsername(user.getUsername())) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "user/signup";
        }

        if (service.existsPhone(user.getPhone())) {
            model.addAttribute("error", "이미 등록된 전화번호입니다.");
            return "user/signup";
        }

        if (user.getEmail() != null && service.existsEmail(user.getEmail())) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "user/signup";
        }

        service.register(user);
        return "redirect:/user/login";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = service.login(username, password);

        if (user == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }

        // 세션 저장
        session.setAttribute("loginUser", user);
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
