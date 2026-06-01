package com.karibu.ride_app_backend.ride.infrastructure.notification;

import com.karibu.ride_app_backend.ride.domain.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminNotificationAdapter implements NotificationPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void notifyAdmin(String message, UUID referenceId) {
        // Dans une vraie application, cela pourrait envoyer un e-mail ou un SMS
        // Pour cet exemple, nous simulons la notification via les logs
        log.info("📢 [NOTIFICATION ADMIN] {} | Ref ID: {}", message, referenceId);
    }
}
