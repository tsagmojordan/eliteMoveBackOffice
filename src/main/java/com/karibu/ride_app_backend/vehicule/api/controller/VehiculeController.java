package com.karibu.ride_app_backend.vehicule.api.controller;

import com.karibu.ride_app_backend.vehicule.api.dto.CreateVehiculeRequest;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeDto;
import com.karibu.ride_app_backend.vehicule.api.mapper.VehiculeMapper;
import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class VehiculeController {

    private final ManageVehiculeUseCase manageVehiculeUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehiculeDto> createVehicule(
            @Valid
            @RequestPart("request") CreateVehiculeRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) {
        Vehicule vehicule = VehiculeMapper.toDomain(request);
        Vehicule created = manageVehiculeUseCase.createVehicule(vehicule, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(VehiculeMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDto> getVehicule(@PathVariable UUID id) {
        Vehicule vehicule = manageVehiculeUseCase.getVehicule(id);
        return ResponseEntity.ok(VehiculeMapper.toDto(vehicule));
    }

    @GetMapping
    public ResponseEntity<List<VehiculeDto>> getAllVehicules() {
        List<VehiculeDto> vehicules = manageVehiculeUseCase.getAllVehicules().stream()
                .map(VehiculeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehiculeDto>> getAvailableVehicules() {
        List<VehiculeDto> availableVehicules = manageVehiculeUseCase.getAvailableVehicules().stream()
                .map(VehiculeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(availableVehicules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDto> updateVehicule(@PathVariable UUID id,
            @Valid @RequestBody CreateVehiculeRequest request) {
        Vehicule vehicule = VehiculeMapper.toDomain(request);
        Vehicule updated = manageVehiculeUseCase.updateVehicule(id, vehicule);
        return ResponseEntity.ok(VehiculeMapper.toDto(updated));
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
