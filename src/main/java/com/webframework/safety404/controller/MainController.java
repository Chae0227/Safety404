package com.webframework.safety404.controller;

import com.webframework.safety404.domain.Verification;
import com.webframework.safety404.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final VerifyService verifyService;

    @GetMapping("/")
    public String main(Model model) {

        // ✅ 승인된 글 최신 3개
        List<Verification> recentPosts =
                verifyService.findRecentApproved(3);

        model.addAttribute("recentPosts", recentPosts);
        return "main/main";
    }
}
