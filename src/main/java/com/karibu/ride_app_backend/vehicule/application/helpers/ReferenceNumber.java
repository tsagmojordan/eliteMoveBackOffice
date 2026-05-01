package com.karibu.ride_app_backend.vehicule.application.helpers;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReferenceNumber {

    static String generate(String keyword) {
        String timesNumber = String.valueOf(LocalDateTime.now().getNano());
        String randomUuid = UUID.randomUUID().toString().substring(0,8);
        return (keyword  + timesNumber + randomUuid).toUpperCase();
    }
}
