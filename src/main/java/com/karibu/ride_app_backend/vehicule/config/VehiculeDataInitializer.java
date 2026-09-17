package com.karibu.ride_app_backend.vehicule.config;

import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeClass;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialiseur de véhicules de démarrage.
 *
 * <p>
 * Crée des véhicules de démonstration avec leurs photos chargées
 * depuis {@code src/main/resources/static/images} en passant par le
 * port {@link ManageVehiculeUseCase}, comme le ferait l'API.
 *
 * <p>
 * Les photos disponibles dans le dossier sont réparties cycliquement
 * entre les véhicules (3 photos par véhicule). Les formats non
 * supportés par le FileManager (jpeg, jpg, png, webp) sont ignorés.
 *
 * <p>
 * Idempotent (ne recrée rien si des véhicules existent déjà) et nettoie
 * le dossier {@code upload/picture/vehicule} avant de seeder pour éviter
 * l'accumulation de fichiers en dev (H2 create-drop reseed à chaque
 * démarrage, avec de nouveaux ReferenceNumber à chaque fois).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VehiculeDataInitializer implements CommandLineRunner {

    private static final String IMAGES_CLASSPATH_PATTERN = "classpath:static/images/*";
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png", "webp");

    /** Doit rester aligné avec {@code FileManagerImpl.DIRECTORY}. */
    private static final String UPLOAD_DIRECTORY = "upload/picture/vehicule";
    /** Doit rester aligné avec {@code FileManagerImpl.THUMBNAIL_DIRECTORY}. */
    private static final String THUMBNAIL_DIRECTORY = "upload/picture/vehicule/thumbnails";

    private final ManageVehiculeUseCase manageVehiculeUseCase;

    @Override
    public void run(final String... args) {
        log.debug("[VehiculeDataInitializer] Initialisation des véhicules de démarrage");

        if (!manageVehiculeUseCase.getAllVehicules().isEmpty()) {
            log.debug("[VehiculeDataInitializer] Véhicules déjà présents — initialisation ignorée");
            return;
        }

        final List<String> photoNames = listAvailablePhotos();
        if (photoNames.size() < 3) {
            log.warn("[VehiculeDataInitializer] Moins de 3 photos exploitables dans static/images — initialisation ignorée");
            return;
        }

        cleanUploadDirectories();

        int photoIndex = 0;
        for (final SeedVehicule spec : SeedVehicule.all()) {
            final List<MultipartFile> photos = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                photos.add(loadPhoto(photoNames.get(photoIndex % photoNames.size())));
                photoIndex++;
            }
            createVehicule(spec, photos);
        }

        log.debug("[VehiculeDataInitializer] Initialisation terminée avec succès : {} véhicule(s)", SeedVehicule.all().size());
    }

    /**
     * Liste les photos du dossier classpath static/images, filtrées sur
     * les extensions supportées par le FileManager.
     */
    private List<String> listAvailablePhotos() {
        final List<String> photoNames = new ArrayList<>();
        try {
            final Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(IMAGES_CLASSPATH_PATTERN);
            for (final Resource resource : resources) {
                final String filename = resource.getFilename();
                if (filename == null) {
                    continue;
                }
                final String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                if (ALLOWED_EXTENSIONS.contains(extension)) {
                    photoNames.add(filename);
                } else {
                    log.warn("[VehiculeDataInitializer] Image ignorée (format non supporté) : {}", filename);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lister les images de seed dans static/images", e);
        }
        photoNames.sort(String::compareTo);
        log.debug("[VehiculeDataInitializer] {} photo(s) exploitables trouvées", photoNames.size());
        return photoNames;
    }

    /**
     * Nettoie les dossiers d'upload avant de seeder (uniquement appelé
     * quand le seed va réellement s'exécuter).
     */
    private void cleanUploadDirectories() {
        for (final String directory : List.of(UPLOAD_DIRECTORY, THUMBNAIL_DIRECTORY)) {
            final File dir = new File(System.getProperty("user.dir") + "/" + directory);
            if (!dir.exists()) {
                continue;
            }
            final File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }
            for (final File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    log.warn("[VehiculeDataInitializer] Impossible de supprimer {}", file.getName(), e);
                }
            }
            log.debug("[VehiculeDataInitializer] Dossier nettoyé : {}", directory);
        }
    }

    private void createVehicule(final SeedVehicule spec, final List<MultipartFile> photos) {
        final Vehicule vehicule = Vehicule.builder()
                .brand(spec.brand())
                .model(spec.model())
                .year(spec.year())
                .licensePlate(spec.licensePlate())
                .vehiculeClass(spec.vehiculeClass())
                .status(spec.status())
                .price(spec.price())
                .build();

        final Vehicule saved = manageVehiculeUseCase.createVehicule(vehicule, photos);
        log.debug("[VehiculeDataInitializer] Véhicule créé : {} {} ({})", saved.getBrand(), saved.getModel(), saved.getId());
    }

    private MultipartFile loadPhoto(final String photoName) {
        try {
            final ClassPathResource resource = new ClassPathResource("static/images/" + photoName);
            final byte[] bytes = resource.getInputStream().readAllBytes();
            return new ByteArrayMultipartFile(photoName, bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger l'image de seed : " + photoName, e);
        }
    }

    /**
     * Jeu de données de seed : 20 véhicules de démonstration.
     */
    private record SeedVehicule(
            String brand,
            String model,
            int year,
            String licensePlate,
            VehiculeClass vehiculeClass,
            VehiculeStatus status,
            int price) {

        static List<SeedVehicule> all() {
            return List.of(
                    new SeedVehicule("Toyota", "Corolla", 2022, "CE-2022-001", VehiculeClass.ECO, VehiculeStatus.AVAILABLE, 5000),
                    new SeedVehicule("Mercedes", "Classe E", 2024, "NO-2024-002", VehiculeClass.PREMIUM, VehiculeStatus.AVAILABLE, 15000),
                    new SeedVehicule("Hyundai", "Accent", 2023, "CE-2023-003", VehiculeClass.ECO, VehiculeStatus.AVAILABLE, 4500),
                    new SeedVehicule("Volkswagen", "Golf 8", 2023, "LT-2023-004", VehiculeClass.CONFORT, VehiculeStatus.AVAILABLE, 8000),
                    new SeedVehicule("Toyota", "Hiace", 2021, "CE-2021-005", VehiculeClass.VAN, VehiculeStatus.AVAILABLE, 12000),
                    new SeedVehicule("BMW", "Série 5", 2023, "NO-2023-006", VehiculeClass.PREMIUM, VehiculeStatus.IN_RIDE, 18000),
                    new SeedVehicule("Renault", "Duster", 2022, "CE-2022-007", VehiculeClass.ECO, VehiculeStatus.MAINTENANCE, 4000),
                    new SeedVehicule("Peugeot", "208", 2023, "LT-2023-008", VehiculeClass.CONFORT, VehiculeStatus.AVAILABLE, 7000),
                    new SeedVehicule("Kia", "Rio", 2022, "CE-2022-009", VehiculeClass.ECO, VehiculeStatus.AVAILABLE, 4000),
                    new SeedVehicule("Mercedes", "Vito", 2023, "NO-2023-010", VehiculeClass.VAN, VehiculeStatus.AVAILABLE, 14000),
                    new SeedVehicule("Ford", "Ranger", 2022, "LT-2022-011", VehiculeClass.CONFORT, VehiculeStatus.MAINTENANCE, 9000),
                    new SeedVehicule("Toyota", "Yaris", 2024, "CE-2024-012", VehiculeClass.ECO, VehiculeStatus.AVAILABLE, 5000),
                    new SeedVehicule("Audi", "A4", 2023, "NO-2023-013", VehiculeClass.PREMIUM, VehiculeStatus.AVAILABLE, 16000),
                    new SeedVehicule("Nissan", "Navara", 2021, "LT-2021-014", VehiculeClass.VAN, VehiculeStatus.AVAILABLE, 11000),
                    new SeedVehicule("Hyundai", "Tucson", 2023, "CE-2023-015", VehiculeClass.CONFORT, VehiculeStatus.IN_RIDE, 8500),
                    new SeedVehicule("Toyota", "Land Cruiser", 2024, "NO-2024-016", VehiculeClass.PREMIUM, VehiculeStatus.AVAILABLE, 25000),
                    new SeedVehicule("Suzuki", "Swift", 2022, "CE-2022-017", VehiculeClass.ECO, VehiculeStatus.OUT_OF_SERVICE, 3500),
                    new SeedVehicule("Mercedes", "Classe C", 2022, "NO-2022-018", VehiculeClass.PREMIUM, VehiculeStatus.AVAILABLE, 14000),
                    new SeedVehicule("Dacia", "Logan", 2023, "LT-2023-019", VehiculeClass.ECO, VehiculeStatus.AVAILABLE, 3800),
                    new SeedVehicule("Hyundai", "Staria", 2024, "NO-2024-020", VehiculeClass.VAN, VehiculeStatus.AVAILABLE, 15000));
        }
    }

    /**
     * Implémentation minimale de {@link MultipartFile} à partir de bytes
     * chargés depuis le classpath (les photos de seed ne proviennent pas d'une requête HTTP).
     */
    private record ByteArrayMultipartFile(String filename, byte[] content) implements MultipartFile {

        @Override
        public String getName() {
            return "photo";
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(final File dest) throws IOException {
            try (InputStream in = getInputStream()) {
                dest.toPath().getParent().toFile().mkdirs();
                Files.copy(in, dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}