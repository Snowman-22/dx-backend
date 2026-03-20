package com.snowman.team2.domain.chat.service;

import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.FastApiChatRequest;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatGatewayService {

    private final RestTemplate restTemplate;

    @Value("${ai.fastapi.base-url:http://localhost:8000}")
    private String fastApiBaseUrl;

    public FastApiChatResponse sendToFastApi(ChatMessage msg) {
        FastApiChatRequest request = new FastApiChatRequest(
                msg.convId(),
                msg.stepCode(),
                msg.assistantText(),
                msg.userText()
        );

        // FastAPI/LangGraph 엔드포인트: POST /ai/chat
        String url = fastApiBaseUrl + "/ai/chat";
        return restTemplate.postForObject(url, request, FastApiChatResponse.class);
    }
}

