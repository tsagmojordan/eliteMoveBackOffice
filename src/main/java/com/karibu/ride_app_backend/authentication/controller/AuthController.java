package com.karibu.ride_app_backend.authentication.controller;

import com.karibu.ride_app_backend.authentication.dto.request.LoginRequest;
import com.karibu.ride_app_backend.authentication.dto.response.AuthResponse;
import com.karibu.ride_app_backend.authentication.service.AuthService;
import com.karibu.ride_app_backend.authentication.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification JWT.
 *
 * <p>
 * Endpoints publics : login, refresh, logout.
 * Aucune logique métier ici — tout est délégué à {@link AuthService}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authentifie un utilisateur et retourne ses tokens JWT.
     *
     * <p>
     * POST /api/v1/auth/login
     *
     * @param request Identifiants de connexion.
     * @return Tokens JWT + informations utilisateur.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody final LoginRequest request) {
        log.debug("[AuthController] Requête de login reçue pour : {}", request.usernameOrEmail());
        final AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", response));
    }

    /**
     * Rafraîchit un access token via un refresh token valide.
     *
     * <p>
     * POST /api/v1/auth/refresh
     *
     * @param authHeader Header Authorization contenant "Bearer <refresh_token>".
     * @return Nouveaux tokens JWT.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader("Authorization") final String authHeader) {
        log.debug("[AuthController] Requête de rafraîchissement de token reçue");
        final String rawToken = authHeader.substring("Bearer ".length());
        final AuthResponse response = authService.refresh(rawToken);
        return ResponseEntity.ok(ApiResponse.success("Token rafraîchi avec succès", response));
    }

    /**
     * Déconnecte l'utilisateur en révoquant son token.
     *
     * <p>
     * POST /api/v1/auth/logout
     *
     * @param authHeader Header Authorization contenant "Bearer <token>".
     * @return Message de confirmation.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") final String authHeader) {
        log.debug("[AuthController] Requête de logout reçue");
        final String rawToken = authHeader.substring("Bearer ".length());
        authService.logout(rawToken);
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie"));
    }

    /**
     * Demande la réinitialisation du mot de passe (Envoi d'email).
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody final com.karibu.ride_app_backend.authentication.dto.request.ResetPasswordRequest request) {
        log.debug("[AuthController] Demande de réinitialisation pour : {}", request.email());
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Si cet email existe, un lien de réinitialisation a été envoyé."));
    }

    /**
     * Confirme la réinitialisation avec le token reçu par email.
     */
    @PostMapping("/confirm-reset-password")
    public ResponseEntity<ApiResponse<Void>> confirmPasswordReset(
            @Valid @RequestBody final com.karibu.ride_app_backend.authentication.dto.request.ConfirmResetPasswordRequest request) {
        log.debug("[AuthController] Confirmation de réinitialisation de mot de passe");
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe réinitialisé avec succès."));
    }
}
