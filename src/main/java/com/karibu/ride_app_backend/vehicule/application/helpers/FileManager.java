package com.karibu.ride_app_backend.vehicule.application.helpers;


import org.springframework.web.multipart.MultipartFile;

public interface FileManager {

    String save(MultipartFile photo);

    public byte[] get(String fileName);

}
