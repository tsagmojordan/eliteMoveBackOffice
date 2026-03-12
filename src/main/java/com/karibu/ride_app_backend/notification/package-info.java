/**
 * Module de notification du back-office Smart Lighting.
 *
 * <p>
 * Ce module gère la distribution des notifications via différents canaux :
 * Email, SMS, WhatsApp, In-App et WebSocket.
 *
 * <p>
 * Il réagit de manière asynchrone aux événements publiés dans le module
 * {@code shared}
 * via Spring Modulith.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Notification Module")
package com.karibu.ride_app_backend.notification;
