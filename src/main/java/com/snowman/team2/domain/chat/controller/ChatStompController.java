package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.ChatReply;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import com.snowman.team2.domain.chat.service.ChatGatewayService;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatGatewayService chatGatewayService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 프론트에서 /app/chat.send 로 메시지를 보내면 처리.
     */
    @MessageMapping("/chat.send")
    public void handleChat(ChatMessage message, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        FastApiChatResponse fastapiResp = chatGatewayService.sendToFastApi(message);

        ChatReply reply = new ChatReply(
                message.convId(),
                fastapiResp.data(),
                fastapiResp.ai_response()
        );

        String destination = "/topic/chat/" + message.convId();
        messagingTemplate.convertAndSend(destination, reply);
    }
}

