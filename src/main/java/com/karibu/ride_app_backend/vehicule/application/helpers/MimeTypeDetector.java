package com.karibu.ride_app_backend.vehicule.application.helpers;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MimeTypeDetector {

    private final Tika tika = new Tika();

    /**
     * Détecte le type MIME d'un fichier MultipartFile
     * @param file le fichier à analyser
     * @return le type MIME détecté (ex: image/jpeg, image/png)
     */
    public String detectMimeType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la détection du type MIME : " + e.getMessage(), e);
        }
    }

    /**
     * Détecte le type MIME d'un fichier par son nom
     * @param fileName le nom du fichier
     * @return le type MIME détecté
     */
    public String detectMimeTypeByName(String fileName) {
        return tika.detect(fileName);
    }
}
