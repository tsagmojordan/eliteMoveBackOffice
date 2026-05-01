package com.karibu.ride_app_backend.vehicule.application.service;

import com.karibu.ride_app_backend.vehicule.application.helpers.FileManager;
import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import com.karibu.ride_app_backend.vehicule.domain.exception.VehiculeNotFoundException;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import com.karibu.ride_app_backend.vehicule.domain.port.out.VehiculeRepository;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class VehiculeService implements ManageVehiculeUseCase {

    private final VehiculeRepository vehiculeRepository;

    private final FileManager fileManager;

    @Override
    @Transactional
    public Vehicule createVehicule(Vehicule vehicule, List<MultipartFile> photos) {
        if (vehicule.getStatus() == null) {
            vehicule.setStatus(VehiculeStatus.AVAILABLE);
        }
        if (photos.size() > 3) {
            throw new IllegalArgumentException("Au moins 3 photos sont requises");
        }
        MultipartFile photo1 = photos.get(0);
        MultipartFile photo2 = photos.get(1);
        MultipartFile photo3 = photos.get(2);
        vehicule.addPrincipalImage(fileManager.save(photo1));
        vehicule.addSecondImage(fileManager.save(photo2));
        vehicule.addSThridImage(fileManager.save(photo3));

        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getVehicule(UUID id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new VehiculeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAllVehicules() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAvailableVehicules() {
        return vehiculeRepository.findAllAvailable();
    }

    @Override
    @Transactional
    public Vehicule updateVehicule(UUID id, Vehicule vehiculeDetails) {
        Vehicule vehicule = getVehicule(id);

        vehicule.setBrand(vehiculeDetails.getBrand());
        vehicule.setModel(vehiculeDetails.getModel());
        vehicule.setYear(vehiculeDetails.getYear());
        vehicule.setLicensePlate(vehiculeDetails.getLicensePlate());
        if (vehiculeDetails.getVehiculeClass() != null) {
            vehicule.setVehiculeClass(vehiculeDetails.getVehiculeClass());
        }
        if (vehiculeDetails.getStatus() != null) {
            vehicule.setStatus(vehiculeDetails.getStatus());
        }

        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public void deleteVehicule(UUID id) {
        Vehicule vehicule = getVehicule(id);
        vehiculeRepository.deleteById(vehicule.getId());
    }

    @Override
    @Transactional
    public void updateVehiculeStatus(UUID id, String statusStr) {
        Vehicule vehicule = getVehicule(id);
        VehiculeStatus status = VehiculeStatus.valueOf(statusStr.toUpperCase());
        vehicule.setStatus(status);
        vehiculeRepository.save(vehicule);
    }
}
