package com.karibu.ride_app_backend.authentication.config;

import com.karibu.ride_app_backend.authentication.utils.ApiResponse;
import com.karibu.ride_app_backend.call.domain.exception.CallNotFoundException;
import com.karibu.ride_app_backend.call.domain.exception.InvalidCallStateException;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions REST.
 *
 * <p>
 * Centralise le traitement de toutes les exceptions applicatives
 * pour garantir des réponses cohérentes et éviter la duplication
 * dans les contrôleurs.
 */
@Slf4j
@RestControllerAdvice
@Profile("prod")
public class GlobalExceptionHandler {

        /**
         * Traite les exceptions de validation Bean Validation (@Valid).
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
                        final MethodArgumentNotValidException ex) {
                log.debug("[GlobalExceptionHandler] Erreurs de validation détectées : {} champ(s) invalide(s)",
                                ex.getBindingResult().getFieldErrorCount());

                final Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        final String fieldName = ((FieldError) error).getField();
                        errors.put(fieldName, error.getDefaultMessage());
                });

                return ResponseEntity
                                .badRequest()
                                .body(new ApiResponse<Map<String, String>>(false, "Erreurs de validation", errors,
                                                HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now()));
        }

        /**
         * Traite les {@link ResponseStatusException} (toutes nos exceptions métier).
         */
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
                        final ResponseStatusException ex) {
                log.debug("[GlobalExceptionHandler] ResponseStatusException : {} - {}",
                                ex.getStatusCode(), ex.getReason());

                return ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ApiResponse.error(ex.getReason(),
                                                HttpStatus.valueOf(ex.getStatusCode().value())));
        }

        /**
         * Traite les accès refusés Spring Security (403).
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
                        final AccessDeniedException ex) {
                log.debug("[GlobalExceptionHandler] Accès refusé : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error("Accès refusé : permission insuffisante",
                                                HttpStatus.FORBIDDEN));
        }

        /**
         * Traite les erreurs d'identifiants Spring Security (401).
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
                        final BadCredentialsException ex) {
                log.debug("[GlobalExceptionHandler] Identifiants invalides");
                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error("Identifiants invalides", HttpStatus.UNAUTHORIZED));
        }

        /**
         * Traite les appels introuvables (404 — Module Call).
         */
        @ExceptionHandler(CallNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleCallNotFoundException(
                        final CallNotFoundException ex) {
                log.debug("[GlobalExceptionHandler] Appel introuvable : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
        }

        /**
         * Traite les transitions d'état invalides sur un appel (409 Conflict — Module
         * Call).
         */
        @ExceptionHandler(InvalidCallStateException.class)
        public ResponseEntity<ApiResponse<Void>> handleInvalidCallStateException(
                        final InvalidCallStateException ex) {
                log.debug("[GlobalExceptionHandler] Transition d'état invalide : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
        }

        /**
         * Traite toutes les exceptions non catchées (500).
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGlobalException(final Exception ex) {
                log.debug("[GlobalExceptionHandler] Erreur interne non gérée : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Une erreur interne s'est produite",
                                                HttpStatus.INTERNAL_SERVER_ERROR));
        }

        @ExceptionHandler(Vehicule.VehiculeException.class)
        public ResponseEntity<ApiResponse<Void>> handleVehiculeException(final Vehicule.VehiculeException ex) {
                log.debug("[GlobalExceptionHandler] Erreur interne non gérée : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(ex.getMessage(),
                                                HttpStatus.INTERNAL_SERVER_ERROR));
        }
}
