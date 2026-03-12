package com.karibu.ride_app_backend.authentication.helpers;


import com.karibu.ride_app_backend.authentication.repository.TokenRepository;
import com.karibu.ride_app_backend.authentication.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre JWT exécuté une seule fois par requête HTTP.
 *
 * <p>
 * Extrait le token du header {@code Authorization},
 * valide sa signature et son état en base (non révoqué),
 * puis alimente le contexte de sécurité Spring.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtHelper jwtHelper;
    private final CustomUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!hasBearerToken(authHeader)) {
            log.debug("[JwtAuthenticationFilter] Pas de token Bearer dans la requête : {}",
                    request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = extractToken(authHeader);
        final String username = extractUsernameQuietly(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("[JwtAuthenticationFilter] Validation du token pour le username : {}", username);
            authenticateIfValid(request, jwt, username);
        }

        filterChain.doFilter(request, response);
    }

    // ===== Méthodes privées =====

    private boolean hasBearerToken(final String authHeader) {
        return authHeader != null && authHeader.startsWith(BEARER_PREFIX);
    }

    private String extractToken(final String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private String extractUsernameQuietly(final String jwt) {
        try {
            return jwtHelper.extractUsername(jwt);
        } catch (Exception ex) {
            log.debug("[JwtAuthenticationFilter] Extraction du username échouée : {}", ex.getMessage());
            return null;
        }
    }

    private void authenticateIfValid(
            final HttpServletRequest request,
            final String jwt,
            final String username) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        final boolean tokenValidInDb = tokenRepository.findByTokenValue(jwt)
                .map(t -> t.isValid())
                .orElse(false);

        if (jwtHelper.isTokenValid(jwt, userDetails) && tokenValidInDb) {
            log.debug("[JwtAuthenticationFilter] Token valide — authentification du contexte pour : {}",
                    username);

            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            log.debug("[JwtAuthenticationFilter] Token invalide ou révoqué pour : {}", username);
        }
    }
}
