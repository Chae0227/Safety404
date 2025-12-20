package com.webframework.safety404.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 발표/과제용 안정 모델
    private static final String MODEL_NAME = "gpt-4o-mini";

    public String ask(String message) {

        System.out.println("===== ChatGPT (GeminiService) 호출 =====");
        System.out.println("사용자 입력: " + message);
        System.out.println("API KEY 존재 여부: " + (apiKey != null && !apiKey.isBlank()));
        System.out.println("사용 모델: " + MODEL_NAME);

        String url = "https://api.openai.com/v1/chat/completions";

        // 🔥 ChatGPT 프롬프트
        Map<String, Object> body = Map.of(
            "model", MODEL_NAME,
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content",
                    """
                    당신은 사용자의 질문에 친절하고 자연스럽게 답변하는 AI입니다.
                    전문가처럼 말하되 과장하지 말고,
                    사용자의 상황을 실제로 돕는 데 집중하세요.
                    """
                ),
                Map.of(
                    "role", "user",
                    "content", message
                )
            ),
            "temperature", 0.7
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // 🔑 핵심

        try {
            ResponseEntity<Map> response =
                restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(body, headers),
                    Map.class
                );

            System.out.println("ChatGPT HTTP status: " + response.getStatusCode());

            Map responseBody = response.getBody();
            if (responseBody == null) return "AI 응답 없음";

            List choices = (List) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "AI 응답 없음";
            }

            Map messageObj = (Map) ((Map) choices.get(0)).get("message");
            Object content = messageObj.get("content");

            return content == null ? "AI 응답 없음" : content.toString();

        } catch (Exception e) {
            System.out.println("🔥 ChatGPT 호출 실패");
            e.printStackTrace();
            return "AI 서버 오류 발생";
        }
    }
}
