package com.karibu.ride_app_backend.call.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration des WebSockets bruts (Raw WebSockets).
 *
 * <p>
 * Ces websockets ne passent pas par STOMP. Ils servent à transporter
 * des streams binaires purs (comme le flux audio des appels).
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class CallWebSocketConfig implements WebSocketConfigurer {

    private final AudioStreamWebSocketHandler audioStreamWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Enregistrement du point d'accès pour le flux audio
        // URL côté client mobile : ws://host:port/api/v1/calls/{callId}/stream/{userId}
        // Ex :
        // ws://localhost:8081/api/v1/calls/123e4567-e89b-12d3.../stream/456e4567-e89b...
        registry.addHandler(audioStreamWebSocketHandler, "/api/v1/calls/*/stream/*")
                .setAllowedOrigins("*");
    }
}
