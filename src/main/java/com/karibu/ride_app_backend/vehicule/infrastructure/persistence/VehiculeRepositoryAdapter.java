package com.karibu.ride_app_backend.vehicule.infrastructure.persistence;

import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import com.karibu.ride_app_backend.vehicule.domain.port.out.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehiculeRepositoryAdapter implements VehiculeRepository {

    private final JpaVehiculeRepository jpaVehiculeRepository;

    @Override
    public Vehicule save(Vehicule vehicule) {
        JpaVehiculeEntity entity = JpaVehiculeEntity.builder()
                .id(vehicule.getId())
                .brand(vehicule.getBrand())
                .model(vehicule.getModel())
                .year(vehicule.getYear())
                .licensePlate(vehicule.getLicensePlate())
                .vehiculeClass(vehicule.getVehiculeClass())
                .principalImagePath(vehicule.getPhoto1())
                .secondImagePath(vehicule.getPhoto2())
                .thirdImagePath(vehicule.getPhoto3())
                .status(vehicule.getStatus())
                .build();
        JpaVehiculeEntity saved = jpaVehiculeRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Vehicule> findById(UUID id) {
        return jpaVehiculeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Vehicule> findAll() {
        return jpaVehiculeRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vehicule> findAllAvailable() {
        return jpaVehiculeRepository.findByStatus(VehiculeStatus.AVAILABLE).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaVehiculeRepository.deleteById(id);
    }

    private Vehicule toDomain(JpaVehiculeEntity entity) {
        return Vehicule.builder()
                .id(entity.getId())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .licensePlate(entity.getLicensePlate())
                .vehiculeClass(entity.getVehiculeClass())
                .status(entity.getStatus())
                .photo1(entity.getPrincipalImagePath())
                .photo2(entity.getSecondImagePath())
                .photo3(entity.getThirdImagePath())
                .build();
    }
}
