package com.webframework.safety404.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final GeminiService geminiService;

    public ChatService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public String chat(String userMessage) {
        return geminiService.ask(userMessage);
    }
}
