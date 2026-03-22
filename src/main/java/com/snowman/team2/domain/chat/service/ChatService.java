package com.snowman.team2.domain.chat.service;

import com.snowman.team2.domain.auth.entity.UserEntity;
import com.snowman.team2.domain.auth.repository.UserRepository;
import com.snowman.team2.domain.chat.dto.PrestartChatResponseDTO;
import com.snowman.team2.domain.chat.dto.ChatEnterResponseDTO;
import com.snowman.team2.domain.chat.dto.StarterPackageInfoResponseDTO;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import com.snowman.team2.domain.chat.repository.ChatRepository;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageEntity;
import com.snowman.team2.domain.starterPackage.entity.StarterPackageType;
import com.snowman.team2.domain.starterPackage.repository.StarterPackageRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final StarterPackageRepository starterPackageRepository;
    private final UserRepository userRepository;

    @Transactional
    public PrestartChatResponseDTO prestartChat(StarterPackageType starterPackageType) {
        StarterPackageEntity starterPackage = starterPackageRepository
                .findByStarterPackageNameAndIsUseTrue(starterPackageType)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "스타터 패키지를 찾을 수 없습니다."));

        if (starterPackage.getIsUse() != null && !starterPackage.getIsUse()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "사용할 수 없는 스타터 패키지입니다.");
        }

        return PrestartChatResponseDTO.builder()
                .starterPackageId(starterPackage.getStarterPackageId())
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

    /**
     * 채팅 시작 버튼에서만 chat_id를 발급한다.
     */
    @Transactional
    public ChatEnterResponseDTO startChat(Long starterPackageId, Long userId) {
        if (starterPackageId == null || userId == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "starter_package_id, user_id가 필요합니다.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "사용자를 찾을 수 없습니다."));

        StarterPackageEntity starterPackage = starterPackageRepository.findById(starterPackageId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "스타터 패키지를 찾을 수 없습니다."));

        if (starterPackage.getIsUse() != null && !starterPackage.getIsUse()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "사용할 수 없는 스타터 패키지입니다.");
        }

        String chatConvId = "chat_" + java.util.UUID.randomUUID();

        ChatEntity chat = ChatEntity.builder()
                .user(user)
                .starterPackage(starterPackage)
                .chatConvId(chatConvId)
                .startDate(LocalDateTime.now())
                .isSelectBlueprint(false)
                .build();

        ChatEntity saved = chatRepository.save(chat);

        return new ChatEnterResponseDTO(
                saved.getChatConvId(),
                userId,
                user.getUserName(),
                starterPackage.getStarterPackageId(),
                starterPackage.getStarterPackageName(),
                starterPackage.getStarterPackageName().getDescription()
        );
    }

    /**
     * 해당 대화(chat_conv_id)에 연결된 스타터 패키지 정보를 조회한다.
     */
    @Transactional(readOnly = true)
    public StarterPackageInfoResponseDTO getStarterPackageForChat(String chatConvId, Long userId) {
        ChatEntity chat = chatRepository.findByChatConvId(chatConvId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "채팅을 찾을 수 없습니다."));

        assertChatOwnedByUser(chat, userId);

        var starter = chat.getStarterPackage();
        if (starter == null || starter.getStarterPackageName() == null) {
            throw new BadRequestException(ErrorCode.DATA_NOT_EXIST, "스타터 패키지 정보를 찾을 수 없습니다.");
        }

        return new StarterPackageInfoResponseDTO(
                starter.getStarterPackageId(),
                starter.getStarterPackageName().name(),
                starter.getStarterPackageName().getDescription()
        );
    }

    private void assertChatOwnedByUser(ChatEntity chat, Long userId) {
        if (chat.getUser() == null
                || chat.getUser().getUserId() == null
                || !Objects.equals(chat.getUser().getUserId(), userId)) {
            throw new UnauthorizedException(ErrorCode.ACCESS_DENIED, "접근 권한이 없습니다.");
        }
    }
}

