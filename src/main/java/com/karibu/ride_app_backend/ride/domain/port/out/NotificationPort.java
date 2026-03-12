package com.karibu.ride_app_backend.ride.domain.port.out;

import java.util.UUID;

public interface NotificationPort {
    void notifyAdmin(String message, UUID referenceId);
}
