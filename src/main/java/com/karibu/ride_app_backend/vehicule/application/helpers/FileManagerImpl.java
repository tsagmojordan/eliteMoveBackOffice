package com.karibu.ride_app_backend.vehicule.application.helpers;

import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileManagerImpl implements FileManager {

    private static final String DIRECTORY = "upload/picture/vehicule";
    private static final String THUMBNAIL_DIRECTORY = "upload/picture/vehicule/thumbnails";
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2 MB
    private final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png", "webp");
    private final MimeTypeDetector mimeTypeDetector;
    private final ThumbnailGenerator thumbnailGenerator;

    @Override
    public String save(MultipartFile photo) {
        if (photo.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("La photo dépasse la taille maximale autorisée (2 MB)");
        }

        String originalFilename = photo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = ReferenceNumber.generate("VEH") + extension;

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase().replaceFirst("\\.", ""))) {
            throw new Vehicule.VehiculeException("Format image non supporté : " + extension);
        }

        // Résoudre le chemin relatif en chemin absolu
        File file = new File(System.getProperty("user.dir") + "/" + DIRECTORY + "/" + newFileName);
        file.getParentFile().mkdirs();

        try {
            photo.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la photo", e);
        }

        return newFileName;
    }

    @Override
    public byte[] get(String fileName) {
        File file = new File(System.getProperty("user.dir") + "/" + DIRECTORY + "/" + fileName);

        if (!file.exists()) {
            throw new RuntimeException("Fichier introuvable : " + fileName);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier : " + fileName, e);
        }
    }

    @Override
    public String getMimeType(String fileName) {
        return mimeTypeDetector.detectMimeTypeByName(fileName);
    }

    /**
     * Sauvegarde une photo et génère sa miniature
     * @param photo le fichier photo
     * @return le path du fichier photo (la miniature est générée automatiquement)
     */
    public String saveWithThumbnail(MultipartFile photo) {
        String photoPath = save(photo);
        
        try {
            byte[] thumbnailData = thumbnailGenerator.generateThumbnail(photo);
            String thumbnailFileName = photoPath.replaceFirst("\\.\\w+$", "") + "_thumb.jpg";
            saveThumbnail(thumbnailFileName, thumbnailData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la miniature pour : " + photoPath, e);
        }
        
        return photoPath;
    }

    /**
     * Sauvegarde les données de miniature
     */
    private void saveThumbnail(String thumbnailFileName, byte[] thumbnailData) {
        File thumbnailFile = new File(System.getProperty("user.dir") + "/" + THUMBNAIL_DIRECTORY + "/" + thumbnailFileName);
        thumbnailFile.getParentFile().mkdirs();
        
        try {
            Files.write(thumbnailFile.toPath(), thumbnailData);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la miniature : " + thumbnailFileName, e);
        }
    }

    /**
     * Récupère le path du thumbnail pour un fichier photo
     */
    public String getThumbnailPath(String photoFileName) {
        return photoFileName.replaceFirst("\\.\\w+$", "") + "_thumb.jpg";
    }

    /**
     * Récupère le thumbnail par son path
     */
    public byte[] getThumbnail(String thumbnailFileName) {
        File file = new File(System.getProperty("user.dir") + "/" + THUMBNAIL_DIRECTORY + "/" + thumbnailFileName);

        if (!file.exists()) {
            throw new RuntimeException("Thumbnail introuvable : " + thumbnailFileName);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du thumbnail : " + thumbnailFileName, e);
        }
    }
}
