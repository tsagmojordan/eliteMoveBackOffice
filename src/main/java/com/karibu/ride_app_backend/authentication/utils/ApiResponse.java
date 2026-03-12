package com.karibu.ride_app_backend.authentication.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Enveloppe standardisée de toutes les réponses REST de l'application.
 *
 * <p>
 * Garantit un format cohérent côté client :
 * 
 * <pre>
 * {
 *   "success": true,
 *   "message": "Opération réussie",
 *   "data": { ... },
 *   "status": 200,
 *   "timestamp": "2026-02-21T00:00:00"
 * }
 * </pre>
 *
 * @param <T> Type de la donnée encapsulée.
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final int status;
    private final LocalDateTime timestamp;

    /**
     * Constructeur principal — utiliser les factory methods de préférence.
     */
    public ApiResponse(
            final boolean success,
            final String message,
            final T data,
            final int status,
            final LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = timestamp;
    }

    // ===== Factory methods =====

    /**
     * Réponse de succès 200 avec données.
     */
    public static <T> ApiResponse<T> success(final String message, final T data) {
        return new ApiResponse<>(true, message, data, HttpStatus.OK.value(), LocalDateTime.now());
    }

    /**
     * Réponse de succès 200 sans données.
     */
    public static <T> ApiResponse<T> success(final String message) {
        return new ApiResponse<>(true, message, null, HttpStatus.OK.value(), LocalDateTime.now());
    }

    /**
     * Réponse de succès 201 Created.
     */
    public static <T> ApiResponse<T> created(final String message, final T data) {
        return new ApiResponse<>(true, message, data, HttpStatus.CREATED.value(), LocalDateTime.now());
    }

    /**
     * Réponse d'erreur.
     */
    public static <T> ApiResponse<T> error(final String message, final HttpStatus status) {
        return new ApiResponse<>(false, message, null, status.value(), LocalDateTime.now());
    }
}
