package com.karibu.ride_app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Point d'entrée de l'application Ride App Backend.
 *
 * <p>
 * {@code @EnableScheduling} active les tâches planifiées (ex:
 * {@code CallTimeoutScheduler}).
 */
@SpringBootApplication
@EnableScheduling
public class RideAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RideAppBackendApplication.class, args);
    }

}
