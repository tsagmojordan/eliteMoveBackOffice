# 🛠️ Plan de correctifs — BACKEND (eliteMoveBackOffice)

> **Contexte** : audit croisé frontend (Android `eliteMove`) ↔ backend (Spring Boot). La plupart des divergences constatées sont corrigées **côté frontend** (voir `../eliteMove/front.md`). Ce plan ne contient que les correctifs qui nécessitent une modification du backend.
>
> **Adresse serveur confirmée** : `http://147.79.118.51:7820` — ne pas modifier.

---

## ⚠️ Règle de non-régression

Les points suivants ressemblent à des « bugs » vus depuis le frontend, mais le contrat est **gelé** — le frontend s'y aligne. **NE PAS** les modifier :

| Sujet | Contrat gelé (inchangé) |
|---|---|
| Refresh token | `POST /api/v1/auth/refresh` — refresh token dans le header `Authorization: Bearer <refresh>`, **pas de body** |
| Notifications | Enveloppe `ApiResponse<PaginatedResponse<InAppNotificationResponse>>` (données dans `data.content`, champ `subject` — pas `title`) ; `unread/count` → `ApiResponse<Long>` ; `read` / `read-all` → `ApiResponse<Void>` |
| Liste utilisateurs | `ApiResponse<PaginatedResponse<UserResponse>>` (`data.content`) |
| Appels REST | `POST /api/v1/calls` → `InitiateCallResponse {callId, message}` **brut** (sans enveloppe) ; accept/decline/end → 204 ; `GET /api/v1/calls/history` → `List<CallResponse>` brut |
| Véhicules | Listes **brutes** `List<VehiculeDto>` / `List<VehiculeWithThumbnailDto>` ; création en **multipart** avec parts `request` (JSON) + `photos` (fichiers, optionnel) ; enums `ECO, CONFORT, PREMIUM, VAN` et `AVAILABLE, IN_RIDE, MAINTENANCE, OUT_OF_SERVICE` |
| Rides | DTOs bruts ; `PATCH /api/v1/rides/{id}/status?status=` ; statuts `REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED` |

---

## 📋 Table des contrats partagés (référence commune avec front.md)

| # | Sujet | Contrat retenu | Qui corrige |
|---|---|---|---|
| C1 | Refresh token | Backend **inchangé** (header `Authorization`) | Frontend seul |
| C2 | Inscription | **Nouvel endpoint** `POST /api/v1/auth/register`, public, voir Tâche B1 | Backend + Frontend |
| C3 | Initier un appel | Backend **inchangé** (`InitiateCallResponse`) | Frontend seul |
| C4 | Notifications | Backend **inchangé** (pagination + `subject`) | Frontend seul |
| C5 | Liste utilisateurs | Backend **inchangé** (pagination) | Frontend seul |
| C6 | Véhicules (photos, enums, multipart) | Backend **inchangé** | Frontend seul |
| C7 | Appels temps réel | **Backend ajoute** le push WebSocket des appels entrants et de la signalisation → voir Tâche B2 | Backend + Frontend |
| C8 | Prix d'un ride | **Backend ajoute** `price` au `RideDto` → voir Tâche B3 | Backend + Frontend |

---

## 🧩 Tâche B1 — Endpoint d'inscription public (contrat C2)

**Problème** : `POST /api/v1/users` est protégé (`@PreAuthorize("hasAuthority('USER_CREATE')")` + `anyRequest().authenticated()`). L'app Android appelle l'inscription **avant authentification** → 401/403. L'inscription depuis l'app est impossible.

**Correctif** : créer un endpoint d'inscription **public** dans `AuthController` :

```
POST /api/v1/auth/register
Body (JSON) : { "firstname", "lastname", "username", "email", "password" }   (= CreateUserRequest existant)
Réponse 201 : ApiResponse<UserResponse>
```

- Chemin sous `/api/v1/auth/**` → automatiquement `permitAll` dans `SecurityConfig` (aucun changement de sécurité requis).
- Réutiliser le use case existant de `UserController` (même validation `@Valid`, mêmes règles : rôles par défaut, username/email uniques, etc.).
- **Ne pas** ouvrir `POST /api/v1/users` en permitAll (il reste réservé aux admins via `USER_CREATE`).
- Mots de passe hachés, réponse sans jamais renvoyer le hash.

**Critères d'acceptation** :
1. `curl -X POST http://147.79.118.51:7820/api/v1/auth/register -H 'Content-Type: application/json' -d '{"firstname":"Test","lastname":"User","username":"testuser","email":"t@t.com","password":"Pass1234"}'` (sans JWT) → 201 avec `ApiResponse<UserResponse>`.
2. L'utilisateur créé peut se connecter via `POST /api/v1/auth/login`.
3. `POST /api/v1/users` reste protégé (401 sans token).

---

## 🧩 Tâche B2 — Appels entrants & signalisation WebRTC via WebSocket (contrat C7)

**Problème** : le backend *accepte* la signalisation (`POST /api/v1/calls/signaling`) mais ne la **délivre jamais** au correspondant. Aucun événement d'appel entrant n'est poussé. Sans cela, deux terminaux ne peuvent pas établir d'appel (l'offer/answer/ICE n'arrivent jamais au callee).

**Correctif** : réutiliser le endpoint STOMP existant `/ws-notifications` (`WebSocketConfig.java` — broker `/topic` et `/queue`, préfixe app `/app`, JWT sur CONNECT) pour pousser les événements d'appel vers l'utilisateur destinataire via `SimpMessagingTemplate.convertAndSendToUser(userId, "/queue/calls", payload)`.

**Contrat des messages WS** (gelé — le frontend s'y aligne) — destination `/user/queue/calls` :

```json
// 1. Appel entrant (poussé au callee au moment du POST /api/v1/calls)
{ "type": "INCOMING_CALL", "call": { ...CallResponse... } }

// 2. Relay de signalisation (poussé au callee à chaque POST /api/v1/calls/signaling)
{ "type": "SIGNAL", "callId": "<UUID>", "signal": { ...payload SDP/ICE telle que reçue... } }

// 3. Mise à jour de statut d'appel (accept/decline/end — poussée aux deux parties)
{ "type": "CALL_STATUS", "callId": "<UUID>", "status": "ACCEPTED|DECLINED|ENDED|MISSED" }
```

**Étapes** :
1. Créer un `CallEventPublisher` (service injectant `SimpMessagingTemplate`).
2. `POST /api/v1/calls` : après création, publier `INCOMING_CALL` vers le `calleeId`.
3. `POST /api/v1/calls/signaling` : stocker le signal (en mémoire/DB, au choix) **et** publier `SIGNAL` vers le participant qui n'est pas l'expéditeur (récupérer caller/callee depuis l'entité `Call`).
4. `accept` / `decline` / `end` : publier `CALL_STATUS` vers les deux parties.
5. Vérifier que les destinations utilisateur (`convertAndSendToUser`) fonctionnent avec la config STOMP actuelle (préfixe user implicite). Si le broker actuel ne supporte pas les user-destinations, ajuster `WebSocketConfig` **sans casser** `/topic` notifications existant.
6. `CallResponse` doit être sérialisable tel quel (champs : `id, callerId, calleeId, callType, status, createdAt, answeredAt, endedAt, durationSeconds, endReason, isActive, isTerminated`).

**Critères d'acceptation** :
1. `POST /api/v1/calls` (avec JWT utilisateur A vers utilisateur B) : B connecté au WS avec son JWT reçoit `INCOMING_CALL`.
2. A envoie `POST /api/v1/calls/signaling` → B reçoit `SIGNAL` avec la payload intacte.
3. `PATCH /api/v1/calls/{id}/accept` → les deux parties reçoivent `CALL_STATUS`.
4. Le flux notifications WS existant continue de fonctionner.

---

## 🧩 Tâche B3 — Prix d'un ride (contrat C8)

**Problème** : le frontend affiche le prix du trajet, mais `RideDto` n'a pas de champ `price`.

**Correctif** :
1. Ajouter `price` (`Double`) au `RideDto` (exposition dans le mapper).
2. Calculer le prix selon une règle métier simple et déterministe — proposition : tarif de base selon la classe du véhicule (`ECO=500`, `CONFORT=1000`, `PREMIUM=2000`, `VAN=3000` FCFA par course, à ajuster avec le client) + persister le prix sur l'entité `Ride` au moment de l'acceptation (pour rester stable même si les tarifs changent).
3. Le prix peut être `null` tant que la course n'est pas acceptée ; non-nul dès `ACCEPTED`.

**Critères d'acceptation** :
1. `POST /api/v1/rides` puis `PATCH .../status?status=ACCEPTED` → `GET /api/v1/rides/{id}` renvoie `price` non nul.
2. `GET /api/v1/rides` et `/user/{userId}` incluent `price`.

---

## 🧩 Tâche B4 — Documentation obsolète

`IMPLEMENTATION_SUMMARY.md` décrit un multipart avec parts `photo1/2/3` : le contrôleur réel utilise `request` + `photos`. Mettre le document à jour pour refléter le code réel (et éviter qu'un futur agent s'aligne sur la doc fausse).

---

## 🧩 Tâche B5 — Vérification globale (à exécuter après B1–B3)

```bash
# Build
./mvnw clean package -DskipTests

# Inscription publique (B1)
curl -i -X POST http://147.79.118.51:7820/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"firstname":"Test","lastname":"User","username":"testuser","email":"t@t.com","password":"Pass1234"}'

# Login + récupération du token
TOKEN=$(curl -s -X POST http://147.79.118.51:7820/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"usernameOrEmail":"t@t.com","password":"Pass1234"}' | jq -r '.data.accessToken')

# Refresh via header (contrat C1 — à ne pas casser)
curl -i -X POST http://147.79.118.51:7820/api/v1/auth/refresh \
  -H "Authorization: Bearer <REFRESH_TOKEN>"

# Véhicules (contrats C6 — à ne pas casser)
curl -s http://147.79.118.51:7820/api/v1/vehicules -H "Authorization: Bearer $TOKEN"
curl -s http://147.79.118.51:7820/api/v1/vehicules/available/with-thumbnails -H "Authorization: Bearer $TOKEN"
curl -i -X POST http://147.79.118.51:7820/api/v1/vehicules \
  -H "Authorization: Bearer $TOKEN" \
  -F 'request={"brand":"Toyota","model":"Corolla","year":2022,"licensePlate":"AB-123-CD","vehiculeClass":"ECO","price":500};type=application/json' \
  -F 'photos=@/tmp/test.jpg'

# Ride + prix (B3)
curl -s -X POST http://147.79.118.51:7820/api/v1/rides -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"userId":"<UUID>","vehiculeId":null,"pickupLocation":"A","dropoffLocation":"B"}'

# Appel + WS (B2) — vérifier avec un client STOMP (wscat/stomp) connecté sur
# http://147.79.118.51:7820/ws-notifications avec Authorization: Bearer $TOKEN
curl -s -X POST http://147.79.118.51:7820/api/v1/calls -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"calleeId":"<UUID_B>","callType":"AUDIO"}'
# → B doit recevoir {"type":"INCOMING_CALL", ...} sur /user/queue/calls
```

**Checklist finale** :
- [ ] B1 : inscription publique 201 sans JWT
- [ ] B2 : INCOMING_CALL / SIGNAL / CALL_STATUS reçus sur `/user/queue/calls`
- [ ] B3 : `price` présent dans RideDto
- [ ] B4 : IMPLEMENTATION_SUMMARY.md à jour
- [ ] Aucun changement sur les contrats gelés (C1, C3, C4, C5, C6)
- [ ] `mvn clean package` OK

---

## 📌 Ce qui N'EST PAS à faire côté backend

- Ne pas ajouter d'enveloppe `ApiResponse` sur rides/véhicules/appels (le frontend lit déjà les DTOs bruts).
- Ne pas changer les noms de champs des DTOs (`subject`, `content`, `callId`...).
- Ne pas modifier les enums `VehiculeClass` / `VehiculeStatus` / `RideStatus` / `CallStatus`.
- Ne pas modifier les chemins existants ni le port (7820).