package com.karibu.ride_app_backend.vehicule.application.helpers;

import org.springframework.web.multipart.MultipartFile;

public interface FileManager {

    String save(MultipartFile photo);

    byte[] get(String fileName);

    String getMimeType(String fileName);

    String saveWithThumbnail(MultipartFile photo);

    String getThumbnailPath(String photoFileName);

    byte[] getThumbnail(String thumbnailFileName);
}
