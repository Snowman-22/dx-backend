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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public void handleChat(ChatMessage message, Principal principal) {
        // STOMP Principal 주입이 환경/설정에 따라 null로 들어올 수 있으므로,
        // 인터셉터에서 주입한 SecurityContext의 Authentication을 기준으로 인증 여부를 판단합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

