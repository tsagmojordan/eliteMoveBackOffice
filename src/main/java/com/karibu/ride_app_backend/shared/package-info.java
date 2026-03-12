/**
 * Module partagé (Shared Kernel) du back-office Smart Lighting.
 *
 * <p>
 * Ce module fournit les contrats d'événements (payloads) utilisés
 * par tous les autres modules pour communiquer via Spring Modulith.
 * Il ne doit contenir <strong>aucune logique métier</strong>,
 * uniquement des records immuables représentant les événements.
 *
 * <p>
 * Convention de nommage des events : {@code [DomainContext][Action]Event}.
 * Exemple : {@code UserCreatedEvent}, {@code NotificationRequestedEvent}.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Shared Kernel", type = ApplicationModule.Type.OPEN)
package com.karibu.ride_app_backend.shared;

import org.springframework.modulith.ApplicationModule;