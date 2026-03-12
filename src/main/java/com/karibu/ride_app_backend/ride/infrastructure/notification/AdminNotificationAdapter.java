package com.karibu.ride_app_backend.ride.infrastructure.notification;

import com.karibu.ride_app_backend.ride.domain.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AdminNotificationAdapter implements NotificationPort {

    @Override
    public void notifyAdmin(String message, UUID referenceId) {
        // Dans une vraie application, cela pourrait envoyer un e-mail ou un SMS
        // Pour cet exemple, nous simulons la notification via les logs
        log.info("📢 [NOTIFICATION ADMIN] {} | Ref ID: {}", message, referenceId);
    }
}
