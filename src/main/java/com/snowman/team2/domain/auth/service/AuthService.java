package com.snowman.team2.domain.auth.service;

import com.snowman.team2.domain.auth.dto.request.LoginRequestDTO;
import com.snowman.team2.domain.auth.dto.request.SignupRequestDTO;
import com.snowman.team2.domain.auth.dto.response.LoginResponseDTO;
import com.snowman.team2.domain.auth.entity.UserEntity;
import com.snowman.team2.domain.auth.repository.UserRepository;
import com.snowman.team2.domain.chat.service.ChatService;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import com.snowman.team2.global.security.JwtTokenProvider;
import com.snowman.team2.global.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

    @Transactional
    public void signup(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorCode.DATA_ALREADY_EXIST, "이미 사용 중인 이메일입니다.");
        }
        if (request.getTermsAccepted() == null || !request.getTermsAccepted()
                || request.getPrivacyAccepted() == null || !request.getPrivacyAccepted()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER, "이용약관 및 개인정보처리방침 동의가 필요합니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity user = request.toEntity(encodedPassword);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        var tokenResponse = jwtTokenProvider.generateToken(authentication);

        return LoginResponseDTO.from(
                userDetails.getUserId(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );
    }
}
