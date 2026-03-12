package com.karibu.ride_app_backend.notification.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Initialisation à chaud du client Twilio lors du démarrage de Spring.
 */
@Slf4j
@Configuration
public class TwilioConfig {

    @Value("${application.twilio.account-sid}")
    private String accountSid;

    @Value("${application.twilio.auth-token}")
    private String authToken;

    @PostConstruct
    public void init() {
        if (!accountSid.equals("test_sid") && !authToken.equals("test_token")) {
            Twilio.init(accountSid, authToken);
            log.info("[TwilioConfig] SDK Twilio initialisé avec succès !");
        } else {
            log.warn("[TwilioConfig] Identifiants Twilio non configurés. Envoi SMS/WhatsApp échouera.");
        }
    }
}
