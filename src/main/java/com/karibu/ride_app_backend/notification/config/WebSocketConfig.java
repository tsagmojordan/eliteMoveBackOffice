package com.karibu.ride_app_backend.notification.config;

import com.karibu.ride_app_backend.authentication.helpers.JwtHelper;
import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Optional;

/**
 * Configuration des WebSockets temps réel (STOMP via SockJS).
 * Intercepte également la connexion STOMP pour authentifier l'utilisateur via
 * JWT.
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Point d'entrée pour les clients front-end : ws://host:port/ws-notifications
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        // Active le broker en mémoire pour "/topic" (broadcast global) et "/queue"
        // (utilisateurs précis)
        registry.enableSimpleBroker("/topic", "/queue");

        // Les clients envoient via /app (si besoin de parler au serveur)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        try {
                            String username = jwtHelper.extractUsername(token);
                            Optional<User> userOpt = userRepository.findByUsername(username);

                            if (userOpt.isPresent() && jwtHelper.isTokenValid(token, userOpt.get())) {
                                User user = userOpt.get();
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        user, null, user.getAuthorities());

                                // RÈGLE CRUCIALE POUR convertAndSendToUser :
                                // Spring Security associe cette authentification à la session WebSocket
                                accessor.setUser(auth);
                                log.debug("[WebSocketConfig] Connexion WebSocket authentifiée pour l'utilisateur: {}",
                                        username);
                            }
                        } catch (Exception e) {
                            log.error("[WebSocketConfig] Échec de l'authentification WebSocket : {}", e.getMessage());
                        }
                    }
                }
                return message;
            }
        });
    }
}
