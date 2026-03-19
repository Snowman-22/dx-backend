package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.ChatReply;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import com.snowman.team2.domain.chat.service.ChatGatewayService;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.domain.reco.service.RecommendationService;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatGatewayService chatGatewayService;
    private final RecommendationService recommendationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 프론트에서 /app/chat.send 로 메시지를 보내면 처리.
     */
    @MessageMapping("/chat.send")
    public void handleChat(ChatMessage message, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long chatId;
        try {
            chatId = Long.valueOf(message.convId());
        } catch (NumberFormatException e) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "유효하지 않은 chat id 입니다.");
        }

        FastApiChatResponse fastapiResp = chatGatewayService.sendToFastApi(message);
        Map<String, Object> dataWithRecommendationIds =
                recommendationService.saveAndAttachRecommendationIds(chatId, fastapiResp.data());

        ChatReply reply = new ChatReply(
                message.convId(),
                dataWithRecommendationIds,
                fastapiResp.ai_response()
        );

        String destination = "/topic/chat/" + message.convId();
        messagingTemplate.convertAndSend(destination, reply);
    }
}

