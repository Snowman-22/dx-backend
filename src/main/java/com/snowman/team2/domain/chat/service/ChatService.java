package com.snowman.team2.domain.chat.service;

import com.snowman.team2.domain.auth.entity.UserEntity;
import com.snowman.team2.domain.chat.dto.PrestartChatResponseDTO;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageEntity;
import com.snowman.team2.domain.starterPackage.repository.StarterPackageRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final StarterPackageRepository starterPackageRepository;

    @Transactional
    public PrestartChatResponseDTO prestartChat(Long starterPackageId) {
        StarterPackageEntity starterPackage = starterPackageRepository.findById(starterPackageId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "스타터 패키지를 찾을 수 없습니다."));

        if (starterPackage.getIsUse() != null && !starterPackage.getIsUse()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "사용할 수 없는 스타터 패키지입니다.");
        }

        ChatEntity chat = ChatEntity.builder()
                .user(null)
                .starterPackage(starterPackage)
                .startDate(LocalDateTime.now())
                .isSelectBlueprint(false)
                .build();

        ChatEntity saved = chatRepository.save(chat);

        return PrestartChatResponseDTO.builder()
                .chatId(saved.getChatId())
                .starterPackageId(starterPackageId)
                .userId(null)
                .message("채팅이 생성되었습니다. 회원가입/로그인 후 이어서 진행하세요.")
                .build();
    }

    @Transactional
    public void attachUserToChat(Long chatId, UserEntity user) {
        if (chatId == null) {
            return;
        }
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));
        chat.assignUser(user);
    }
}

