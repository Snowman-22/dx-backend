package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.ChatReply;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import com.snowman.team2.domain.chat.service.ChatGatewayService;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.domain.reco.service.RecommendationService;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatGatewayService chatGatewayService;
    private final RecommendationService recommendationService;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 프론트에서 /app/chat.send 로 메시지를 보내면 처리.
     */
    @MessageMapping("/chat.send")
    public void handleChat(
            ChatMessage message,
            @Header(SimpMessageHeaderAccessor.USER_HEADER) Authentication authentication,
            Principal principal
    ) {
        // Principal 파라미터는 환경/세부 매핑에 따라 null일 수 있습니다.
        // 대신 SimpMessageHeaderAccessor의 simpUser 헤더에서 Authentication을 읽습니다.
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        String chatConvId = message.convId();
        ChatEntity chat = chatRepository.findByChatConvId(chatConvId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        FastApiChatResponse fastapiResp = chatGatewayService.sendToFastApi(message);
        Map<String, Object> dataWithRecommendationIds =
                recommendationService.saveAndAttachRecommendationIds(chat.getChatId(), chatConvId, fastapiResp.data());

        ChatReply reply = new ChatReply(
                message.convId(),
                dataWithRecommendationIds,
                fastapiResp.ai_response()
        );

        String destination = "/topic/chat/" + message.convId();
        messagingTemplate.convertAndSend(destination, reply);
    }
}

