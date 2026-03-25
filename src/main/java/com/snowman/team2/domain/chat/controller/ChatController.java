package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.ChatEnterResponseDTO;
import com.snowman.team2.domain.chat.dto.ChatStartRequestDTO;
import com.snowman.team2.domain.chat.dto.ConversationHistoryResponseDTO;
import com.snowman.team2.domain.chat.dto.PrestartChatRequestDTO;
import com.snowman.team2.domain.chat.dto.PrestartChatResponseDTO;
import com.snowman.team2.domain.chat.dto.StarterPackageInfoResponseDTO;
import com.snowman.team2.domain.chat.service.ChatService;
import com.snowman.team2.global.userDetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    /**
     * 로그인 전: 스타터 패키지 선택 시 Chat row를 미리 생성한다.
     * user_id는 null로 두고, 회원가입/로그인 시점에 user_id를 채운다.
     */
    @PostMapping("/prestart")
    public ResponseEntity<PrestartChatResponseDTO> prestartChat(
            @Valid @RequestBody PrestartChatRequestDTO request
    ) {
        return ResponseEntity.ok(chatService.prestartChat(request.starterPackageType()));
    }

    /**
     * 로그인 후 채팅하기 버튼을 눌렀을 때,
     * 해당 starter_package_id로 chat_id를 발급하고 user / starter package 정보를 프론트에 내려준다.
     */
    @PostMapping("/start")
    public ResponseEntity<ChatEnterResponseDTO> startChat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChatStartRequestDTO request
    ) {
        return ResponseEntity.ok(chatService.startChat(request.starterPackageId(), userDetails.getUserId()));
    }

    /**
     * 채팅에 연결된 스타터 패키지(선택했던 패키지) 정보 조회.
     * path의 chatId는 chat_conv_id 문자열.
     */
    @GetMapping("/{chatId}/starter-package")
    public ResponseEntity<StarterPackageInfoResponseDTO> getStarterPackage(
            @PathVariable String chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(chatService.getStarterPackageForChat(chatId, userDetails.getUserId()));
    }

    @GetMapping("/{chatId}/history")
    public ResponseEntity<ConversationHistoryResponseDTO> getConversationHistory(
            @PathVariable String chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(chatService.getConversationHistory(chatId, userDetails.getUserId()));
    }

    @GetMapping("/product-specs/{modelId}")
    public ResponseEntity<Map<String, Object>> getProductSpecDetails(
            @PathVariable String modelId
    ) {
        return ResponseEntity.ok(chatService.getProductSpecDetails(modelId));
    }
}
