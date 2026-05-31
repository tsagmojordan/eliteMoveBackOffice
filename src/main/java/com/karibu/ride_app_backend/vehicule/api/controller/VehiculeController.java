package com.karibu.ride_app_backend.vehicule.api.controller;

import com.karibu.ride_app_backend.vehicule.api.dto.CreateVehiculeRequest;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeDto;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculePhotoResponse;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeWithThumbnailDto;
import com.karibu.ride_app_backend.vehicule.api.mapper.VehiculeMapper;
import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vehicules")
@RequiredArgsConstructor
@Slf4j
public class VehiculeController {

    private final ManageVehiculeUseCase manageVehiculeUseCase;
    private final VehiculeMapper vehiculeMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehiculeDto> createVehicule(
            @Valid
            @RequestPart("request") CreateVehiculeRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) {
        Vehicule vehicule = VehiculeMapper.toDomain(request);
        log.info("[VEHICULE CONTROLLER] request to create vehicule received: {}", vehicule);
        Vehicule created = manageVehiculeUseCase.createVehicule(vehicule, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDto> getVehicule(@PathVariable UUID id) {
        Vehicule vehicule = manageVehiculeUseCase.getVehicule(id);
        return ResponseEntity.ok(vehiculeMapper.toDto(vehicule));
    }

    @GetMapping("/{id}/principalImage")
    public ResponseEntity<byte []> getVehiculeImage(@PathVariable UUID id) {
        return ResponseEntity.ok(manageVehiculeUseCase.getVehiculeWithPrincipalImage(id));
    }

    @GetMapping("/{id}/photo1")
    public ResponseEntity<byte[]> getPhoto1(@PathVariable UUID id) {
        log.info("[VEHICULE CONTROLLER] Récupération de photo1 pour le véhicule: {}", id);
        VehiculePhotoResponse photoResponse = manageVehiculeUseCase.getPhoto1ByVehiculeId(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photoResponse.getMimeType()))
                .header("Content-Disposition", "inline; filename=\"" + photoResponse.getFileName() + "\"")
                .body(photoResponse.getImageData());
    }

    @GetMapping("/{id}/photo2")
    public ResponseEntity<byte[]> getPhoto2(@PathVariable UUID id) {
        log.info("[VEHICULE CONTROLLER] Récupération de photo2 pour le véhicule: {}", id);
        VehiculePhotoResponse photoResponse = manageVehiculeUseCase.getPhoto2ByVehiculeId(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photoResponse.getMimeType()))
                .header("Content-Disposition", "inline; filename=\"" + photoResponse.getFileName() + "\"")
                .body(photoResponse.getImageData());
    }

    @GetMapping("/{id}/photo3")
    public ResponseEntity<byte[]> getPhoto3(@PathVariable UUID id) {
        log.info("[VEHICULE CONTROLLER] Récupération de photo3 pour le véhicule: {}", id);
        VehiculePhotoResponse photoResponse = manageVehiculeUseCase.getPhoto3ByVehiculeId(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photoResponse.getMimeType()))
                .header("Content-Disposition", "inline; filename=\"" + photoResponse.getFileName() + "\"")
                .body(photoResponse.getImageData());
    }

    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable UUID id) {
        log.info("[VEHICULE CONTROLLER] Récupération du thumbnail pour le véhicule: {}", id);
        VehiculePhotoResponse thumbnailResponse = manageVehiculeUseCase.getThumbnailByVehiculeId(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-Disposition", "inline; filename=\"" + thumbnailResponse.getFileName() + "\"")
                .body(thumbnailResponse.getImageData());
    }

    @GetMapping
    public ResponseEntity<List<VehiculeDto>> getAllVehicules() {
        List<VehiculeDto> vehicules = manageVehiculeUseCase.getAllVehicules().stream()
                .map(vehiculeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehiculeDto>> getAvailableVehicules() {
        List<VehiculeDto> availableVehicules = manageVehiculeUseCase.getAvailableVehicules().stream()
                .map(vehiculeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(availableVehicules);
    }

    @GetMapping("/with-thumbnails")
    public ResponseEntity<List<VehiculeWithThumbnailDto>> getAllVehiculesWithThumbnails() {
        log.info("[VEHICULE CONTROLLER] Récupération de tous les véhicules avec thumbnails");
        List<VehiculeWithThumbnailDto> vehicules = manageVehiculeUseCase.getAllVehiculesWithThumbnails();
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/available/with-thumbnails")
    public ResponseEntity<List<VehiculeWithThumbnailDto>> getAvailableVehiculesWithThumbnails() {
        log.info("[VEHICULE CONTROLLER] Récupération des véhicules disponibles avec thumbnails");
        List<VehiculeWithThumbnailDto> vehicules = manageVehiculeUseCase.getAvailableVehiculesWithThumbnails();
        return ResponseEntity.ok(vehicules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDto> updateVehicule(@PathVariable UUID id,
            @Valid @RequestBody CreateVehiculeRequest request) {
        Vehicule vehicule = VehiculeMapper.toDomain(request);
        Vehicule updated = manageVehiculeUseCase.updateVehicule(id, vehicule);
        return ResponseEntity.ok(vehiculeMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable UUID id) {
        manageVehiculeUseCase.deleteVehicule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateVehiculeStatus(@PathVariable UUID id, @RequestParam String status) {
        manageVehiculeUseCase.updateVehiculeStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
