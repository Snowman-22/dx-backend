package com.snowman.team2.global.config;

import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.UnauthorizedException;
import com.snowman.team2.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtStompAuthChannelInterceptor(jwtTokenProvider));
    }

    /**
     * STOMP CONNECT/SEND/SUBSCRIBE 프레임에서 Authorization 헤더(JWT)를 확인해서
     * SecurityContext에 인증을 주입한다.
     *
     * 프론트는 STOMP connect 시 Authorization: Bearer <accessToken> 헤더를 같이 보내야 한다.
     */
    private static class JwtStompAuthChannelInterceptor implements ChannelInterceptor {

        private final JwtTokenProvider jwtTokenProvider;

        private JwtStompAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            // STOMP 명령은 StompHeaderAccessor에서 읽어야 합니다.
            StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(message);
            StompCommand command = stompAccessor.getCommand();

            // 디버깅용: 인터셉터가 실제로 호출되는지 최우선 확인
            log.error("STOMP interceptor preSend hit. command={}", command);
            if (command == null) {
                return message;
            }

            // 인증이 필요한 프레임만 검사
            if (command == CONNECT || command == SUBSCRIBE || command == SEND) {
                String authorization = stompAccessor.getFirstNativeHeader("Authorization");
                if (authorization == null) {
                    authorization = stompAccessor.getFirstNativeHeader("authorization");
                }

                log.warn("STOMP {} authHeaderPresent={}", command, authorization != null && !authorization.isBlank());

                if (authorization == null || authorization.isBlank()) {
                    // CONNECT부터 토큰이 없으면 인증 불가
                    throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
                }

                String token = authorization;
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                if (!jwtTokenProvider.validateToken(token)) {
                    throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
                }

                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                stompAccessor.setUser(authentication);

                // @MessageMapping 메서드의 Principal 주입이 필요하면 simpUser에도 세팅합니다.
                SimpMessageHeaderAccessor simpAccessor = SimpMessageHeaderAccessor.wrap(message);
                simpAccessor.setUser(authentication);

                log.warn("STOMP {} authInjected={}", command, simpAccessor.getUser() != null);
            }

            return message;
        }
    }
}

