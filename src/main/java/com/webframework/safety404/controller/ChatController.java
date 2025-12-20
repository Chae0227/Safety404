package com.webframework.safety404.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat")
    public String chat() {
        return "chat/chat"; 
        // templates/chat/chat.html
    }
    
    @GetMapping("/chat/404")
    public String safety404() {
        return "chat/404"; // templates/chat/404.html
    }
}
