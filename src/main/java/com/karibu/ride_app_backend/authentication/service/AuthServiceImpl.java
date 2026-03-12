package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.LoginRequest;
import com.karibu.ride_app_backend.authentication.dto.response.AuthResponse;
import com.karibu.ride_app_backend.authentication.helpers.JwtHelper;
import com.karibu.ride_app_backend.authentication.mapper.UserMapper;
import com.karibu.ride_app_backend.authentication.model.Token;
import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.dto.request.ResetPasswordRequest;
import com.karibu.ride_app_backend.authentication.dto.request.ConfirmResetPasswordRequest;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.enums.NotificationPriority;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import java.util.Map;
import java.util.Set;
import com.karibu.ride_app_backend.authentication.repository.TokenRepository;
import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceNotFoundException;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service d'authentification JWT.
 *
 * <p>
 * Orchestre le login, la génération des tokens,
 * la révocation des anciens tokens et le rafraîchissement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtHelper jwtHelper;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentifie un utilisateur et génère ses tokens JWT.
     *
     * <p>
     * Révoque tous les anciens tokens valides avant d'en émettre de nouveaux.
     *
     * @param request Identifiants de connexion.
     * @return {@link AuthResponse} contenant access token et refresh token.
     * @throws UnauthorizedException en cas d'identifiants invalides.
     */
    @Transactional
    public AuthResponse login(final LoginRequest request) {
        log.debug("[AuthService] Tentative de connexion pour : {}", request.usernameOrEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(),
                            request.password()));
        } catch (BadCredentialsException ex) {
            log.debug("[AuthService] Échec d'authentification pour : {}", request.usernameOrEmail());
            throw new UnauthorizedException("Identifiants invalides");
        }

        final User user = resolveUser(request.usernameOrEmail());

        final String accessToken = jwtHelper.generateAccessToken(user);
        final String refreshToken = jwtHelper.generateRefreshToken(user);

        revokeAllUserTokens(user);
        persistToken(user, accessToken);

        log.debug("[AuthService] Authentification réussie pour l'utilisateur id={}", user.getId());
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * Génère un nouvel access token à partir d'un refresh token valide.
     *
     * @param rawRefreshToken Valeur brute du refresh token (sans "Bearer ").
     * @return {@link AuthResponse} avec le nouvel access token.
     * @throws UnauthorizedException si le refresh token est invalide ou expiré.
     */
    @Transactional
    public AuthResponse refresh(final String rawRefreshToken) {
        log.debug("[AuthService] Demande de rafraîchissement de token");

        final String username = extractUsernameOrThrow(rawRefreshToken);
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + username));

        if (!jwtHelper.isTokenValid(rawRefreshToken, user)) {
            log.debug("[AuthService] Refresh token invalide ou expiré pour : {}", username);
            throw new UnauthorizedException("Refresh token invalide ou expiré");
        }

        final String newAccessToken = jwtHelper.generateAccessToken(user);
        revokeAllUserTokens(user);
        persistToken(user, newAccessToken);

        log.debug("[AuthService] Nouveau access token émis pour l'utilisateur id={}", user.getId());
        return buildAuthResponse(user, newAccessToken, rawRefreshToken);
    }

    /**
     * Révoque explicitement le token d'accès (logout).
     *
     * @param rawToken Valeur brute du token JWT.
     */
    @Transactional
    public void logout(final String rawToken) {
        log.debug("[AuthService] Révocation du token lors du logout");
        tokenRepository.findByTokenValue(rawToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setExpired(true);
                    tokenRepository.save(token);
                    log.debug("[AuthService] Token révoqué avec succès");
                });
    }

    // ===== Méthodes privées =====

    private User resolveUser(final String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + usernameOrEmail));
    }

    private void revokeAllUserTokens(final User user) {
        final List<Token> validTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
        log.debug(
                "[AuthService] {} token(s) révoqué(s) pour l'utilisateur id={}",
                validTokens.size(), user.getId());
    }

    private void persistToken(final User user, final String tokenValue) {
        final Token token = Token.builder()
                .user(user)
                .tokenValue(tokenValue)
                .tokenType(Token.TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
        log.debug("[AuthService] Token persisté en base pour l'utilisateur id={}", user.getId());
    }

    private String extractUsernameOrThrow(final String token) {
        try {
            return jwtHelper.extractUsername(token);
        } catch (Exception ex) {
            log.debug("[AuthService] Extraction du username depuis le token échouée : {}", ex.getMessage());
            throw new UnauthorizedException("Token invalide");
        }
    }

    private AuthResponse buildAuthResponse(
            final User user,
            final String accessToken,
            final String refreshToken) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtHelper.getJwtExpiration() / 1000,
                userMapper.toResponse(user));
    }

    @Transactional
    @Override
    public void requestPasswordReset(ResetPasswordRequest request) {
        log.debug("[AuthService] Demande de réinitialisation pour : {}", request.email());
        final User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Générer un token unique pour le reset
        final String resetTokenValue = UUID.randomUUID().toString();
        final Token resetToken = Token.builder()
                .user(user)
                .tokenValue(resetTokenValue)
                .tokenType(Token.TokenType.RESET_PASSWORD)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(resetToken);

        // Envoyer la notification
        final NotificationRequestedEvent resetEvent = NotificationRequestedEvent.of(
                user.getId(),
                user.getEmail(),
                null,
                user.getFirstname() + " " + user.getLastname(),
                Set.of(NotificationChannel.EMAIL),
                NotificationPriority.HIGH,
                "RESET_PASSWORD",
                Map.of("username", user.getUsername(), "token", resetTokenValue),
                "Réinitialisation de votre mot de passe");
        eventPublisher.publishEvent(resetEvent);
        log.debug("[AuthService] Email de réinitialisation déclenché pour l'utilisateur id={}", user.getId());
    }

    @Transactional
    @Override
    public void confirmPasswordReset(ConfirmResetPasswordRequest request) {
        log.debug("[AuthService] Confirmation de la réinitialisation de mot de passe");
        final Token token = tokenRepository.findByTokenValue(request.token())
                .orElseThrow(() -> new UnauthorizedException("Token invalide ou introuvable"));

        if (!token.isValid() || token.getTokenType() != Token.TokenType.RESET_PASSWORD) {
            throw new UnauthorizedException("Ce lien de réinitialisation est expiré ou n'est plus valide.");
        }

        final User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Révoquer le token pour usage unique
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);

        // Tuer tous les Bearer tokens existants car le mot de passe vient de changer
        revokeAllUserTokens(user);

        log.debug("[AuthService] Le mot de passe a été réinitialisé avec succès pour id={}", user.getId());
    }

}
