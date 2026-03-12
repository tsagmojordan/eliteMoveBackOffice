package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Envoyer un signal de négociation WebRTC.
 */
public interface SendWebRTCSignalUseCase {

    void handle(SendSignalCommand command);

    record SendSignalCommand(UUID callId, UUID senderId, Object signalPayload) {
    }
}
