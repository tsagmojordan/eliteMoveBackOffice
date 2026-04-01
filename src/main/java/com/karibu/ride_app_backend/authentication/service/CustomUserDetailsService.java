package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation de {@link UserDetailsService} pour Spring Security.
 *
 * <p>
 * Charge l'utilisateur par username OU email pour permettre
 * la connexion avec les deux identifiants.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
//    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("[CustomUserDetailsService] Chargement de l'utilisateur : {}", usernameOrEmail);
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    log.debug(
                            "[CustomUserDetailsService] Utilisateur introuvable : {}",
                            usernameOrEmail);
                    return new UsernameNotFoundException(
                            "Utilisateur introuvable : " + usernameOrEmail);
                });
    }
}
