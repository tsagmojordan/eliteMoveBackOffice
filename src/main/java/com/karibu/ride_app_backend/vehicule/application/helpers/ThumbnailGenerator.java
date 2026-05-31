package com.karibu.ride_app_backend.vehicule.application.helpers;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ThumbnailGenerator {

    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    /**
     * Génère une miniature d'une image MultipartFile
     * @param imageFile le fichier image
     * @return les données de l'image miniaturisée
     */
    public byte[] generateThumbnail(MultipartFile imageFile) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(imageFile.getBytes()))
                    .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération de la miniature : " + e.getMessage(), e);
        }
    }

    /**
     * Génère une miniature à partir d'un tableau de bytes
     * @param imageBytes les données brutes de l'image
     * @return les données de l'image miniaturisée
     */
    public byte[] generateThumbnailFromBytes(byte[] imageBytes) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(imageBytes))
                    .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération de la miniature : " + e.getMessage(), e);
        }
    }
}
