package com.karibu.ride_app_backend.authentication.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper utilitaire pour la génération et la validation des tokens JWT.
 *
 * <p>
 * Centralise toute la logique cryptographique JWT (HMAC-SHA256).
 * Ne doit pas être utilisé directement par les contrôleurs.
 */
@Slf4j
@Component
public class JwtHelper {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * -- GETTER --
     *  Retourne la durée d'expiration de l'access token en millisecondes.
     *
     * @return Durée en ms.
     */
    @Getter
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // ===== Extraction =====

    /**
     * Extrait le sujet (username) du token JWT.
     *
     * @param token JWT brut.
     * @return Username encodé dans le claim "sub".
     */
    public String extractUsername(final String token) {
        log.debug("[JwtHelper] Extraction du username depuis le token JWT");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait un claim spécifique du token.
     *
     * @param token          JWT brut.
     * @param claimsResolver Fonction d'extraction.
     * @param <T>            Type du claim.
     * @return Valeur du claim.
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ===== Génération =====

    /**
     * Génère un access token JWT pour l'utilisateur donné.
     *
     * @param userDetails Détails Spring Security de l'utilisateur.
     * @return Token JWT signé.
     */
    public String generateAccessToken(final UserDetails userDetails) {
        log.debug("[JwtHelper] Génération d'un access token pour : {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    /**
     * Génère un refresh token JWT pour l'utilisateur donné.
     *
     * @param userDetails Détails Spring Security de l'utilisateur.
     * @return Refresh token JWT signé.
     */
    public String generateRefreshToken(final UserDetails userDetails) {
        log.debug("[JwtHelper] Génération d'un refresh token pour : {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    // ===== Validation =====

    /**
     * Vérifie si le token est valide pour l'utilisateur donné.
     *
     * @param token       JWT brut.
     * @param userDetails Détails de l'utilisateur.
     * @return {@code true} si le token est valide et non expiré.
     */
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        final boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        log.debug("[JwtHelper] Validation du token pour {} : {}", username, valid);
        return valid;
    }

    // ===== Méthodes privées =====

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String generateToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails,
            final long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
