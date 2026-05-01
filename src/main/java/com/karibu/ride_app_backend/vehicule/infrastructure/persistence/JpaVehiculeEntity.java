package com.karibu.ride_app_backend.vehicule.infrastructure.persistence;

import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeClass;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "vehicules")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JpaVehiculeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year_of_manufacture", nullable = false)
    private int year;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "image1")
    private String principalImagePath;

    @Column(name = "image2")
    private String secondImagePath;

    @Column(name = "image3")
    private String thirdImagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicule_class", nullable = false)
    private VehiculeClass vehiculeClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehiculeStatus status;
}
