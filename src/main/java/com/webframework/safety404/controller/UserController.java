package com.webframework.safety404.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

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

    // 아이디 중복확인 API (AJAX)
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username) {
        return service.existsUsername(username);
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(
            User user,
            @RequestParam(name = "detailAddress", required = false) String detailAddress,
            Model model
    ) {

        // 🔥 이메일 입력 안 했으면 "" → null 로 변환 (중복 문제 해결 핵심)
        if (user.getEmail() != null && user.getEmail().trim().isEmpty()) {
            user.setEmail(null);
        }

        // 아이디 중복 체크
        if (service.existsUsername(user.getUsername())) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "user/signup";
        }

        // 전화번호 중복 체크
        if (service.existsPhone(user.getPhone())) {
            model.addAttribute("error", "이미 등록된 전화번호입니다.");
            return "user/signup";
        }

        // 이메일 중복 체크 (null일 때는 검사 X)
        if (user.getEmail() != null && service.existsEmail(user.getEmail())) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "user/signup";
        }

        // 주소 처리
        if (detailAddress != null && !detailAddress.isBlank()) {
            if (user.getAddress() != null) {
                user.setAddress(user.getAddress() + " " + detailAddress);
            } else {
                user.setAddress(detailAddress);
            }
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
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        User user = service.login(username, password);

        if (user == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }

        session.setAttribute("loginUser", user);
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("logoutSuccess", true);  // 홈화면에서 alert 띄울 데이터
        return "redirect:/";
    }
}
