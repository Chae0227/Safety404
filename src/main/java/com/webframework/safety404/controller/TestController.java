package com.webframework.safety404.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-break")
    public String testBreak() {
        return "test-break";
    }
}
