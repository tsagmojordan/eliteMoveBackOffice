package com.karibu.ride_app_backend.vehicule.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculePhotoResponse {
    private byte[] imageData;
    private String mimeType;
    private String fileName;
}
