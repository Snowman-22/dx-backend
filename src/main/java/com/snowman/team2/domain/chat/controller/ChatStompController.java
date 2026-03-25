package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.ChatReply;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import com.snowman.team2.domain.chat.service.ChatGatewayService;
import com.snowman.team2.domain.chat.service.ChatTitleService;
import com.snowman.team2.domain.chat.service.ConversationDynamoService;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.domain.reco.service.RecommendationService;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatGatewayService chatGatewayService;
    private final ChatTitleService chatTitleService;
    private final ConversationDynamoService conversationDynamoService;
    private final RecommendationService recommendationService;
    private final ChatRepository chatRepository;
    private final S3StorageService s3StorageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 프론트에서 /app/chat.send 로 메시지를 보내면 처리.
     */
    @MessageMapping("/chat.send")
    public void handleChat(ChatMessage message) {
        String chatConvId = message.convId();
        ChatEntity chat = chatRepository.findByChatConvId(chatConvId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        chatTitleService.updateChatTitleIfAbsent(chat, message);
        conversationDynamoService.appendUserMessage(chat, message);
        FastApiChatResponse fastapiResp = chatGatewayService.sendToFastApi(message);
        conversationDynamoService.appendAssistantMessage(chat, fastapiResp);
        Map<String, Object> dataWithRecommendationIds =
                recommendationService.saveAndAttachRecommendationIds(chat.getChatId(), chatConvId, fastapiResp.data());
        Map<String, Object> dataWithPresignedUrls = applyPresignedUrls(dataWithRecommendationIds);

        ChatReply reply = new ChatReply(
                message.convId(),
                dataWithPresignedUrls,
                fastapiResp.aiResponse()
        );

        String destination = "/topic/chat/" + message.convId();
        messagingTemplate.convertAndSend(destination, reply);
    }

    private Map<String, Object> applyPresignedUrls(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        return transformMap(source);
    }

    private Map<String, Object> transformMap(Map<String, Object> input) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = entry.getKey();
            out.put(key, transformValue(key, entry.getValue()));
        }
        return out;
    }

    private Object transformValue(String keyHint, Object value) {
        if (value instanceof Map<?, ?> mapValue) {
            Map<String, Object> converted = new LinkedHashMap<>();
            for (Map.Entry<?, ?> nested : mapValue.entrySet()) {
                String nestedKey = nested.getKey() == null ? "" : String.valueOf(nested.getKey());
                converted.put(nestedKey, transformValue(nestedKey, nested.getValue()));
            }
            return converted;
        }

        if (value instanceof List<?> listValue) {
            List<Object> converted = new ArrayList<>(listValue.size());
            for (Object item : listValue) {
                converted.add(transformValue(keyHint, item));
            }
            return converted;
        }

        if (value instanceof String strValue && isImageKey(keyHint) && isLikelyS3ObjectKey(strValue)) {
            try {
                return s3StorageService.presignedGetUrl(strValue);
            } catch (Exception ignored) {
                // S3 객체 키가 아니거나 presign 실패 시 원본 값을 그대로 반환합니다.
                return strValue;
            }
        }

        return value;
    }

    private boolean isImageKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        String normalized = key.toLowerCase(Locale.ROOT);
        return normalized.contains("image_url")
                || normalized.contains("imageurl")
                || normalized.contains("img_url")
                || normalized.contains("imgurl");
    }

    private boolean isLikelyS3ObjectKey(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return !(normalized.startsWith("http://")
                || normalized.startsWith("https://")
                || normalized.startsWith("data:"));
    }
}
