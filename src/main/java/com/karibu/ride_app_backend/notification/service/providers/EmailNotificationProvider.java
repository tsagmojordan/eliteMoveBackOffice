package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

/**
 * Provider pour l'envoi de vrais e-mails HTML avec Gmail et Thymeleaf.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void sendNotification(final NotificationRequestedEvent event) {
        if (event.recipientEmail() == null || event.recipientEmail().isEmpty()) {
            log.warn("[EmailProvider] Échec: Email destinataire manquant pour l'événement ID {}", event.eventId());
            return;
        }

        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // Charger les variables dynamiques dans le Template HTML
            final Context context = new Context(LocaleContextHolder.getLocale());
            if (event.templateVariables() != null) {
                context.setVariables(event.templateVariables());
            }

            // Exécution de Thymeleaf (charge /resources/mail/{templateCode}.html)
            final String htmlContent = templateEngine.process(event.templateCode(), context);

            helper.setTo(event.recipientEmail());
            helper.setSubject(event.subject() != null ? event.subject() : "Notification SmartLighting");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[EmailProvider] Email HTML '{}' envoyé avec succès à : {}", event.templateCode(),
                    event.recipientEmail());

        } catch (MessagingException e) {
            log.error("[EmailProvider] Erreur critique lors de l'envoi de l'email : {}", e.getMessage(), e);
        }
    }
}
