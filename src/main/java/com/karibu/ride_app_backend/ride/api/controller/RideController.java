package com.karibu.ride_app_backend.ride.api.controller;

import com.karibu.ride_app_backend.ride.api.dto.CreateRideRequest;
import com.karibu.ride_app_backend.ride.api.dto.RideDto;
import com.karibu.ride_app_backend.ride.api.mapper.RideMapper;
import com.karibu.ride_app_backend.ride.application.port.in.ManageRideUseCase;
import com.karibu.ride_app_backend.ride.domain.model.Ride;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final ManageRideUseCase manageRideUseCase;

    @PostMapping
    public ResponseEntity<RideDto> requestRide(@Valid @RequestBody CreateRideRequest request) {
        Ride ride = RideMapper.toDomain(request);
        Ride created = manageRideUseCase.requestRide(ride);
        return ResponseEntity.status(HttpStatus.CREATED).body(RideMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDto> getRide(@PathVariable UUID id) {
        Ride ride = manageRideUseCase.getRide(id);
        return ResponseEntity.ok(RideMapper.toDto(ride));
    }

    @GetMapping
    public ResponseEntity<List<RideDto>> getAllRides() {
        List<RideDto> rides = manageRideUseCase.getAllRides().stream()
                .map(RideMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RideDto>> getRidesByUser(@PathVariable UUID userId) {
        List<RideDto> rides = manageRideUseCase.getRidesByUserId(userId).stream()
                .map(RideMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rides);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideDto> updateRideStatus(@PathVariable UUID id, @RequestParam String status) {
        Ride updatedRide = manageRideUseCase.updateRideStatus(id, status);
        return ResponseEntity.ok(RideMapper.toDto(updatedRide));
    }
}
