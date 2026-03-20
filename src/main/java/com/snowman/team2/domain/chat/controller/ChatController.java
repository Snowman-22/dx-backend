package com.snowman.team2.domain.chat.controller;

import com.snowman.team2.domain.chat.dto.PrestartChatRequestDTO;
import com.snowman.team2.domain.chat.dto.PrestartChatResponseDTO;
import com.snowman.team2.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

