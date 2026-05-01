package com.karibu.ride_app_backend.vehicule.application.port.in;

import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import jakarta.mail.Multipart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ManageVehiculeUseCase {
    Vehicule createVehicule(Vehicule vehicule, List<MultipartFile> photos);

    Vehicule getVehicule(UUID id);

    List<Vehicule> getAllVehicules();

    List<Vehicule> getAvailableVehicules();

    Vehicule updateVehicule(UUID id, Vehicule vehicule);

    void deleteVehicule(UUID id);

    void updateVehiculeStatus(UUID id, String status);
}
