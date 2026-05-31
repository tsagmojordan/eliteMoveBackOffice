# 🧪 Guide de Test des Endpoints - Photo Véhicule

## 📡 Base URL
```
http://localhost:8080/api/v1/vehicules
```

## 1️⃣ Créer un Véhicule avec Photos

### Request: POST /api/v1/vehicules
**Content-Type**: `multipart/form-data`

```bash
curl -X POST http://localhost:8080/api/v1/vehicules \
  -F 'request={
    "brand":"Tesla",
    "model":"Model 3",
    "year":2024,
    "licensePlate":"AB-123-CD",
    "vehiculeClass":"PREMIUM",
    "price":45000
  };type=application/json' \
  -F 'photos=@photo1.jpg' \
  -F 'photos=@photo2.jpg' \
  -F 'photos=@photo3.jpg'
```

**Response: 201 Created**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "brand": "Tesla",
  "model": "Model 3",
  "year": 2024,
  "licensePlate": "AB-123-CD",
  "vehiculeClass": "PREMIUM",
  "status": "AVAILABLE",
  "price": 45000,
  "photo1": "VEH_XyZ1a2b3.jpg",
  "photo2": "VEH_AbC4d5e6.jpg",
  "photo3": "VEH_DeF7g8h9.jpg",
  "photo1MimeType": "image/jpeg",
  "photo2MimeType": "image/jpeg",
  "photo3MimeType": "image/png",
  "photo1ThumbnailPath": "VEH_XyZ1a2b3_thumb.jpg"
}
```

---

## 2️⃣ Récupérer une Photo Individuelle

### Request: GET /api/v1/vehicules/{id}/photo1

```bash
curl -X GET http://localhost:8080/api/v1/vehicules/550e8400-e29b-41d4-a716-446655440000/photo1 \
  -o photo_downloaded.jpg
```

**Response: 200 OK**
- **Content-Type**: Détecté automatiquement (image/jpeg, image/png, etc.)
- **Body**: Données binaires de l'image
- **Headers**:
  ```
  Content-Type: image/jpeg
  Content-Disposition: inline; filename="VEH_XyZ1a2b3.jpg"
  ```

---

## 3️⃣ Récupérer une Miniature

### Request: GET /api/v1/vehicules/{id}/thumbnail

```bash
curl -X GET http://localhost:8080/api/v1/vehicules/550e8400-e29b-41d4-a716-446655440000/thumbnail \
  -o thumbnail_200x200.jpg
```

**Response: 200 OK**
- **Content-Type**: `image/jpeg`
- **Size**: ~20-50 KB (réduction 70-80% vs original)
- **Dimensions**: 200x200 pixels
- **Headers**:
  ```
  Content-Type: image/jpeg
  Content-Disposition: inline; filename="VEH_XyZ1a2b3_thumb.jpg"
  ```

---

## 4️⃣ Récupérer Liste avec Thumbnails en Base64

### Request: GET /api/v1/vehicules/with-thumbnails

```bash
curl -X GET http://localhost:8080/api/v1/vehicules/with-thumbnails
```

**Response: 200 OK**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "brand": "Tesla",
    "model": "Model 3",
    "year": 2024,
    "licensePlate": "AB-123-CD",
    "vehiculeClass": "PREMIUM",
    "status": "AVAILABLE",
    "price": 45000,
    "photo1": "VEH_XyZ1a2b3.jpg",
    "photo2": "VEH_AbC4d5e6.jpg",
    "photo3": "VEH_DeF7g8h9.jpg",
    "photo1MimeType": "image/jpeg",
    "photo1ThumbnailPath": "VEH_XyZ1a2b3_thumb.jpg",
    "thumbnailBase64": "/9j/4AAQSkZJRgABAgAAZABkAAD/2wBDAAgGBgcGBQgH..."
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    ...
  }
]
```

---

## 5️⃣ Récupérer Véhicules Disponibles avec Thumbnails

### Request: GET /api/v1/vehicules/available/with-thumbnails

```bash
curl -X GET http://localhost:8080/api/v1/vehicules/available/with-thumbnails
```

**Response: 200 OK**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "brand": "Tesla",
    "model": "Model 3",
    "status": "AVAILABLE",
    ...
    "thumbnailBase64": "/9j/4AAQSkZJRgABAgAAZABkAAD/2wBDAAgGBgcGBQgH..."
  }
]
```

---

## 6️⃣ Récupérer Détails d'un Véhicule

### Request: GET /api/v1/vehicules/{id}

```bash
curl -X GET http://localhost:8080/api/v1/vehicules/550e8400-e29b-41d4-a716-446655440000
```

**Response: 200 OK**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "brand": "Tesla",
  "model": "Model 3",
  "year": 2024,
  "licensePlate": "AB-123-CD",
  "vehiculeClass": "PREMIUM",
  "status": "AVAILABLE",
  "price": 45000,
  "photo1": "VEH_XyZ1a2b3.jpg",
  "photo1MimeType": "image/jpeg",
  "photo1ThumbnailPath": "VEH_XyZ1a2b3_thumb.jpg",
  ...
}
```

---

## 📊 Types MIME Supportés

Les types MIME suivants sont détectés automatiquement:

| Format | MIME Type | Support |
|--------|-----------|---------|
| JPEG | image/jpeg | ✅ |
| PNG | image/png | ✅ |
| WebP | image/webp | ✅ |
| GIF | image/gif | ✅ |
| BMP | image/bmp | ✅ |
| TIFF | image/tiff | ✅ |

---

## 🛠️ Outils de Test

### cURL
```bash
# Créer un véhicule
curl -X POST http://localhost:8080/api/v1/vehicules \
  -F 'request={"brand":"Tesla",...};type=application/json' \
  -F 'photos=@image1.jpg' -F 'photos=@image2.jpg' -F 'photos=@image3.jpg'

# Récupérer photo
curl -X GET http://localhost:8080/api/v1/vehicules/{id}/photo1 -o photo.jpg

# Récupérer liste avec thumbnails
curl -X GET http://localhost:8080/api/v1/vehicules/with-thumbnails | jq '.'
```

### Postman

1. **Créer Collection**: RIDE_APP_BACKEND
2. **Créer Requests**:
   - POST /api/v1/vehicules (body: form-data)
   - GET /api/v1/vehicules/{id}/photo1
   - GET /api/v1/vehicules/{id}/thumbnail
   - GET /api/v1/vehicules/with-thumbnails

3. **Variables d'Environment**:
   ```
   {{base_url}} = http://localhost:8080
   {{vehicule_id}} = (copié de la réponse POST)
   ```

---

## ⚠️ Codes d'Erreur

| Code | Scenario |
|------|----------|
| 201 | Véhicule créé avec succès |
| 200 | Récupération réussie |
| 400 | Paramètres invalides ou fichiers manquants |
| 404 | Véhicule ou photo non trouvé |
| 413 | Photo > 2 MB |
| 415 | Format image non supporté |

---

## 🔍 Vérifications

### ✅ Vérifier les Fichiers Sauvegardés

```bash
# Vérifier les photos originales
ls -la upload/picture/vehicule/

# Vérifier les thumbnails
ls -la upload/picture/vehicule/thumbnails/

# Vérifier taille
du -sh upload/picture/vehicule/*
```

### ✅ Vérifier la Base de Données

```sql
SELECT id, brand, photo1, photo1_mime_type, photo1_thumbnail_path 
FROM vehicules 
WHERE id = '550e8400-e29b-41d4-a716-446655440000';
```

---

## 📝 Notes

- Les thumbnails sont générés automatiquement lors de la création
- Les MIME types sont détectés par Apache Tika
- Les données Base64 sont encodées côté serveur pour optimiser les listes
- Le chemin du thumbnail est stocké séparément en BD
- Les images originales et thumbnails sont stockés en répertoires distincts

