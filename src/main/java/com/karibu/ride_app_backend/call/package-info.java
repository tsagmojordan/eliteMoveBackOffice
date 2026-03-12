/**
 * Module {@code call} — Appels audio entre utilisateurs.
 *
 * <p>
 * Ce module gère l'intégralité du cycle de vie d'un appel audio :
 * <ul>
 * <li>Initiation d'un appel (sonnerie sur l'app mobile via WebSocket/FCM)</li>
 * <li>Signalisation WebRTC (offre/réponse/ICE candidates)</li>
 * <li>Acceptation, refus, et fin d'appel</li>
 * <li>Gestion de l'historique des appels</li>
 * </ul>
 *
 * <p>
 * Architecture : Hexagonale (Ports &amp; Adapters) conforme à la Clean
 * Architecture.
 * <ul>
 * <li>{@code domain} : Entités, Value Objects, Ports (interfaces)</li>
 * <li>{@code application} : Use cases (orchestration métier)</li>
 * <li>{@code infrastructure} : Adaptateurs (JPA, WebSocket, FCM)</li>
 * <li>{@code api} : Contrôleur REST + DTOs + Mappers</li>
 * </ul>
 *
 * <p>
 * Communication inter-modules : via événements Spring Modulith publiés
 * dans {@code shared.event}.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Call Module")
package com.karibu.ride_app_backend.call;
