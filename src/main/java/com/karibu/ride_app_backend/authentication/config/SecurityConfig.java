package com.karibu.ride_app_backend.authentication.config;

import com.karibu.ride_app_backend.authentication.helpers.JwtAuthenticationFilter;
import com.karibu.ride_app_backend.authentication.helpers.JwtHelper;
import com.karibu.ride_app_backend.authentication.repository.TokenRepository;
import com.karibu.ride_app_backend.authentication.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration Spring Security pour l'application.
 *
 * <p>
 * Architecture STATELESS : pas de session HTTP, tout passe par JWT.
 * {@code @EnableMethodSecurity} active les annotations {@code @PreAuthorize}
 * pour le contrôle d'accès basé sur les permissions.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/h2-console/**",
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private final JwtHelper jwtHelper;
    private final CustomUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    /**
     * Chaîne de filtres de sécurité principale.
     *
     * <p>
     * Ordre des filtres : JWT → UsernamePassword.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        log.debug("[SecurityConfig] Configuration de la chaîne de filtres de sécurité");

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtHelper, userDetailsService,
                tokenRepository);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("[SecurityConfig] Chaîne de filtres configurée avec succès");
        return http.build();
    }

    /**
     * Provider d'authentification DAO utilisant {@link CustomUserDetailsService}
     * et {@link BCryptPasswordEncoder}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Gestionnaire d'authentification Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encodeur de mots de passe BCrypt (force 12).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
