package com.karibu.ride_app_backend.authentication.config;

import com.karibu.ride_app_backend.authentication.utils.ApiResponse;
import com.karibu.ride_app_backend.call.domain.exception.CallNotFoundException;
import com.karibu.ride_app_backend.call.domain.exception.InvalidCallStateException;
import com.karibu.ride_app_backend.vehicule.domain.exception.VehiculeNotFoundException;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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
         * Traite les véhicules introuvables (404 — Module Vehicule).
         */
        @ExceptionHandler(VehiculeNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleVehiculeNotFoundException(
                        final VehiculeNotFoundException ex) {
                log.debug("[GlobalExceptionHandler] Véhicule introuvable : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
        }

        /**
         * Traite les violations de règles métier (400) : nombre de photos, taille
         * de fichier, transitions de statut invalides, etc.
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
                        final IllegalArgumentException ex) {
                log.debug("[GlobalExceptionHandler] Argument invalide : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
        }

        /**
         * Traite les uploads dépassant la limite multipart (413 Payload Too Large).
         */
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(
                        final MaxUploadSizeExceededException ex) {
                log.debug("[GlobalExceptionHandler] Upload trop volumineux : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(ApiResponse.error(
                                                "La photo dépasse la taille maximale autorisée (2 MB)",
                                                HttpStatus.PAYLOAD_TOO_LARGE));
        }

        /**
         * Traite les violations d'unicité en base (409 Conflict) — ex. plaque
         * d'immatriculation déjà utilisée.
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
                        final DataIntegrityViolationException ex) {
                log.debug("[GlobalExceptionHandler] Violation d'intégrité : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(
                                                "Cette plaque d'immatriculation est déjà utilisée par un autre véhicule",
                                                HttpStatus.CONFLICT));
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

        /**
         * Traite les erreurs métier véhicule (400) — ex. format d'image non supporté.
         */
        @ExceptionHandler(Vehicule.VehiculeException.class)
        public ResponseEntity<ApiResponse<Void>> handleVehiculeException(final Vehicule.VehiculeException ex) {
                log.debug("[GlobalExceptionHandler] Erreur métier véhicule : {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
        }
}
