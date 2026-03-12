/**
 * Module d'authentification du back-office Smart Lighting.
 *
 * <p>
 * Ce module gère :
 * <ul>
 * <li>L'authentification JWT (login, refresh, logout)</li>
 * <li>La gestion des utilisateurs (CRUD)</li>
 * <li>La gestion des rôles (CRUD + assignation de permissions)</li>
 * <li>La gestion des permissions (CRUD)</li>
 * </ul>
 *
 * <p>
 * Interface publique exposée aux autres modules :
 * <ul>
 * <li>{@link cm.smartlighting.backoffice.authentication.service.UserService}</li>
 * <li>{@link cm.smartlighting.backoffice.authentication.service.AuthService}</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Authentication Module", type = ApplicationModule.Type.OPEN)
package com.karibu.ride_app_backend.authentication;

import org.springframework.modulith.ApplicationModule;