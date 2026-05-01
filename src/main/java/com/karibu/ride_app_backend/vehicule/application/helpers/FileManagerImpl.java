package com.karibu.ride_app_backend.vehicule.application.helpers;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class FileManagerImpl implements FileManager {

    private static final String DIRECTORY = "upload/picture/vehicule";

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB


    @Override
    public String save(MultipartFile photo) {
        if (photo.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("La photo dépasse la taille maximale autorisée (5 MB)");
        }

        String originalFilename = photo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = ReferenceNumber.generate("VEH") + extension;

        // Résoudre le chemin relatif en chemin absolu
        File file = new File(System.getProperty("user.dir") + "/" + DIRECTORY + newFileName);
        file.getParentFile().mkdirs(); // Créer les dossiers si inexistants

        try {

            photo.transferTo(file);

        } catch (IOException e) {

            throw new RuntimeException("Erreur lors de la sauvegarde de la photo", e);
        }

        return newFileName;
    }

    @Override
    public byte[] get(String fileName) {
        File file = new File(System.getProperty("user.dir") + "/" + DIRECTORY + fileName);

        if (!file.exists()) {
            throw new RuntimeException("Fichier introuvable : " + fileName);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier : " + fileName, e);
        }
    }
}
