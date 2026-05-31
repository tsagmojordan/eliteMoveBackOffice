package com.karibu.ride_app_backend.vehicule.application.service;

import com.karibu.ride_app_backend.vehicule.api.dto.VehiculePhotoResponse;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeWithThumbnailDto;
import com.karibu.ride_app_backend.vehicule.application.helpers.FileManager;
import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import com.karibu.ride_app_backend.vehicule.domain.exception.VehiculeNotFoundException;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import com.karibu.ride_app_backend.vehicule.domain.model.value_object.VehiculeLocation;
import com.karibu.ride_app_backend.vehicule.domain.port.out.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculeService implements ManageVehiculeUseCase {

    private final VehiculeRepository vehiculeRepository;
    private final FileManager fileManager;

    @Override
    @Transactional
    public Vehicule createVehicule(Vehicule vehicule, List<MultipartFile> photos) {
        if (vehicule.getStatus() == null) {
            vehicule.setStatus(VehiculeStatus.AVAILABLE);
        }
        if (photos == null || photos.size() < 3) {
            throw new IllegalArgumentException("Au moins trois photos sont requises");
        }
        
        MultipartFile photo1 = photos.get(0);
        MultipartFile photo2 = photos.get(1);
        MultipartFile photo3 = photos.get(2);
        
        // Photo 1 - Sauvegarder avec génération de miniature
        String photo1Path = fileManager.saveWithThumbnail(photo1);
        String photo1MimeType = fileManager.getMimeType(photo1Path);
        String photo1ThumbnailPath = fileManager.getThumbnailPath(photo1Path);
        vehicule.addPrincipalImage(photo1Path);
        vehicule.setPhoto1MimeType(photo1MimeType);
        vehicule.setPhoto1ThumbnailPath(photo1ThumbnailPath);

        // Photo 2
        String photo2Path = fileManager.save(photo2);
        String photo2MimeType = fileManager.getMimeType(photo2Path);
        vehicule.addSecondImage(photo2Path);
        vehicule.setPhoto2MimeType(photo2MimeType);

        // Photo 3
        String photo3Path = fileManager.save(photo3);
        String photo3MimeType = fileManager.getMimeType(photo3Path);
        vehicule.addSThridImage(photo3Path);
        vehicule.setPhoto3MimeType(photo3MimeType);

        vehicule.setLocation(new VehiculeLocation(0.1, 0.1));

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

    @Override
    @Transactional
    public byte[] getVehiculeWithPrincipalImage(UUID id) {
        Vehicule vehicule = getVehicule(id);
        return fileManager.get(vehicule.getPhoto1());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculePhotoResponse getPhoto1ByVehiculeId(UUID id) {
        Vehicule vehicule = getVehicule(id);
        if (vehicule.getPhoto1() == null) {
            throw new RuntimeException("La photo1 n'existe pas pour le véhicule: " + id);
        }
        
        byte[] imageData = fileManager.get(vehicule.getPhoto1());
        return VehiculePhotoResponse.builder()
                .imageData(imageData)
                .mimeType(vehicule.getPhoto1MimeType())
                .fileName(vehicule.getPhoto1())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculePhotoResponse getPhoto2ByVehiculeId(UUID id) {
        Vehicule vehicule = getVehicule(id);
        if (vehicule.getPhoto2() == null) {
            throw new RuntimeException("La photo2 n'existe pas pour le véhicule: " + id);
        }
        
        byte[] imageData = fileManager.get(vehicule.getPhoto2());
        return VehiculePhotoResponse.builder()
                .imageData(imageData)
                .mimeType(vehicule.getPhoto2MimeType())
                .fileName(vehicule.getPhoto2())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculePhotoResponse getPhoto3ByVehiculeId(UUID id) {
        Vehicule vehicule = getVehicule(id);
        if (vehicule.getPhoto3() == null) {
            throw new RuntimeException("La photo3 n'existe pas pour le véhicule: " + id);
        }
        
        byte[] imageData = fileManager.get(vehicule.getPhoto3());
        return VehiculePhotoResponse.builder()
                .imageData(imageData)
                .mimeType(vehicule.getPhoto3MimeType())
                .fileName(vehicule.getPhoto3())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculePhotoResponse getThumbnailByVehiculeId(UUID id) {
        Vehicule vehicule = getVehicule(id);
        if (vehicule.getPhoto1ThumbnailPath() == null) {
            throw new RuntimeException("La miniature n'existe pas pour le véhicule: " + id);
        }
        
        byte[] imageData = fileManager.getThumbnail(vehicule.getPhoto1ThumbnailPath());
        return VehiculePhotoResponse.builder()
                .imageData(imageData)
                .mimeType("image/jpeg")
                .fileName(vehicule.getPhoto1ThumbnailPath())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeWithThumbnailDto> getAllVehiculesWithThumbnails() {
        return vehiculeRepository.findAll().stream()
                .map(this::toVehiculeWithThumbnailDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeWithThumbnailDto> getAvailableVehiculesWithThumbnails() {
        return vehiculeRepository.findAllAvailable().stream()
                .map(this::toVehiculeWithThumbnailDto)
                .toList();
    }

    private VehiculeWithThumbnailDto toVehiculeWithThumbnailDto(Vehicule vehicule) {
        String thumbnailBase64 = "";
        if (vehicule.getPhoto1ThumbnailPath() != null) {
            try {
                byte[] thumbnailData = fileManager.getThumbnail(vehicule.getPhoto1ThumbnailPath());
                thumbnailBase64 = Base64.getEncoder().encodeToString(thumbnailData);
            } catch (Exception e) {
                log.warn("Erreur lors de la récupération du thumbnail pour le véhicule: {}", vehicule.getId(), e);
            }
        }

        return VehiculeWithThumbnailDto.builder()
                .id(vehicule.getId())
                .brand(vehicule.getBrand())
                .model(vehicule.getModel())
                .year(vehicule.getYear())
                .licensePlate(vehicule.getLicensePlate())
                .vehiculeClass(vehicule.getVehiculeClass())
                .status(vehicule.getStatus())
                .price(vehicule.getPrice())
                .photo1(vehicule.getPhoto1())
                .photo2(vehicule.getPhoto2())
                .photo3(vehicule.getPhoto3())
                .photo1MimeType(vehicule.getPhoto1MimeType())
                .photo1ThumbnailPath(vehicule.getPhoto1ThumbnailPath())
                .thumbnailBase64(thumbnailBase64)
                .build();
    }
}
