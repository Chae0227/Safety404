package com.webframework.safety404.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.webframework.safety404.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;

    public ChatApiController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String reply = chatService.chat(message);
        return Map.of("reply", reply);
    }
}
