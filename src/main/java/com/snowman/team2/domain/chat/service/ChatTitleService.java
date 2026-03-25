package com.snowman.team2.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ChatTitleService {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final List<String> TITLE_SUFFIXES = List.of(
            "홈퍼니싱 추천",
            "공간 가전·가구 추천",
            "집 꾸미기 추천",
            "맞춤 홈퍼니싱",
            "추천 패키지",
            "인테리어 추천",
            "공간 스타일링 추천",
            "가전·가구 큐레이션",
            "공간 구성 추천",
            "맞춤 가전 추천",
            "맞춤 가구 추천",
            "라이프스타일 추천",
            "공간 플랜 추천",
            "홈스타일링 추천",
            "집 인테리어 제안",
            "공간 연출 추천"
    );

    private final ChatRepository chatRepository;
    private final ConversationDynamoService conversationDynamoService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void updateChatTitleIfAbsent(ChatEntity chat, ChatMessage message) {
        if (chat.getChatTitle() != null && !chat.getChatTitle().isBlank()) {
            return;
        }

        String generatedTitle = generateTitle(message);
        if (generatedTitle == null || generatedTitle.isBlank()) {
            return;
        }

        chat.updateChatTitle(generatedTitle);
        chatRepository.save(chat);
        conversationDynamoService.updateChatTitle(chat.getChatConvId(), generatedTitle);
    }

    private String generateTitle(ChatMessage message) {
        String candidate = extractReadableText(message.userText());
        if (candidate == null || candidate.isBlank()) {
            candidate = message.assistantText();
        }
        if (candidate == null) {
            return null;
        }

        String normalized = candidate
                .replaceAll("\\s+", " ")
                .replaceAll("[\\r\\n\\t]+", " ")
                .trim();

        if (normalized.isBlank()) {
            return null;
        }

        String suffix = TITLE_SUFFIXES.get(ThreadLocalRandom.current().nextInt(TITLE_SUFFIXES.size()));
        int maxPrefixLength = Math.max(1, MAX_TITLE_LENGTH - suffix.length() - 1);
        String prefix = normalized.length() <= maxPrefixLength
                ? normalized
                : normalized.substring(0, maxPrefixLength).trim();

        if (prefix.isBlank()) {
            return suffix;
        }
        return prefix + " " + suffix;
    }

    private String extractReadableText(Object userText) {
        if (userText == null) {
            return null;
        }
        if (userText instanceof String str) {
            return str;
        }
        try {
            return objectMapper.writeValueAsString(userText);
        } catch (JsonProcessingException e) {
            return String.valueOf(userText);
        }
    }
}
