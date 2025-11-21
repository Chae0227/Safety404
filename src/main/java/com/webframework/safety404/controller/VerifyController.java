package com.webframework.safety404.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VerifyController {

    @GetMapping("/verify")
    public String verify() {
        return "verify/verify";
    }
}
