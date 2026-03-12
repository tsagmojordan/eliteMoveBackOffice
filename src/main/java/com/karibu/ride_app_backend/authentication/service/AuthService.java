package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.LoginRequest;
import com.karibu.ride_app_backend.authentication.dto.response.AuthResponse;

/**
 * Interface du service d'authentification JWT.
 */
public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String rawRefreshToken);

    void logout(String rawToken);

    void requestPasswordReset(com.karibu.ride_app_backend.authentication.dto.request.ResetPasswordRequest request);

    void confirmPasswordReset(com.karibu.ride_app_backend.authentication.dto.request.ConfirmResetPasswordRequest request);
}
