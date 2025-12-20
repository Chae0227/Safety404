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

    // ===============================
    // 회원가입
    // ===============================
    @GetMapping("/signup")
    public String signupForm() {
        return "user/signup";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username) {
        return service.existsUsername(username);
    }

    @PostMapping("/signup")
    public String signup(
            User user,
            @RequestParam(name = "detailAddress", required = false) String detailAddress,
            Model model
    ) {

        if (user.getEmail() != null && user.getEmail().trim().isEmpty()) {
            user.setEmail(null);
        }

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

    // ===============================
    // 로그인 / 로그아웃
    // ===============================
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

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

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("logoutSuccess", true);
        return "redirect:/";
    }

    // ===============================
    // 마이페이지 (조회)
    // ===============================
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", loginUser);
        return "user/mypage";
    }

    // ===============================
    // 마이페이지 (정보 수정)
    // ===============================
    @PostMapping("/mypage")
    public String updateMyPage(
            User formUser,
            HttpSession session,
            RedirectAttributes ra
    ) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/user/login";
        }

        // 🔐 본인만 수정 가능
        if (!loginUser.getId().equals(formUser.getId())) {
            return "redirect:/user/mypage";
        }

        // 이메일 빈값 처리
        if (formUser.getEmail() != null && formUser.getEmail().trim().isEmpty()) {
            formUser.setEmail(null);
        }

        service.updateMyInfo(formUser);

        // 세션 갱신
        User updatedUser = service.findById(loginUser.getId());
        session.setAttribute("loginUser", updatedUser);

        ra.addFlashAttribute("success", true);
        return "redirect:/user/mypage";
    }
}
