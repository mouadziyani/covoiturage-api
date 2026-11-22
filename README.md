# Covoiturage API

API REST de covoiturage développée avec **Spring Boot**. Elle permet aux conducteurs de publier des trajets (avec des points d'arrêt intermédiaires) et aux passagers de réserver des places sur un segment du trajet.

## Stack technique

| Élément        | Version / Outil                     |
|----------------|-------------------------------------|
| Java           | 17                                  |
| Framework      | Spring Boot 4 (Web MVC, Data JPA, Validation) |
| Base de données| PostgreSQL 17 (via Docker)          |
| Mapping        | MapStruct 1.6                       |
| Boilerplate    | Lombok                              |
| Documentation  | springdoc-openapi (Swagger UI)      |
| Build          | Maven (wrapper inclus)              |

## Architecture

Le code est organisé **par fonctionnalité** (package-by-feature) sous `ma.youcode.covoiturageapi` :

```
car/          Véhicules des conducteurs
driver/       Conducteurs
passager/     Passagers
trip/         Trajets (création, recherche, terminer, annuler)
stoppoint/    Points d'arrêt d'un trajet
reservation/  Réservations des passagers
user/         Classe abstraite User (commune Driver / Passager)
common/       DTO partagés (PageResponse)
enums/        StatutTrajet, StatutReservation
exception/    Exceptions métier + GlobalExceptionHandler
configuration/ Configuration OpenAPI
```

Chaque module suit la même structure : `controller` → `service` (interface + implémentation) → `repository`, avec des `dto` (request/response), un `mapper` MapStruct et une `entity` JPA.

## Modèle de données

- **Driver** / **Passager** héritent de `User` (`firstName`, `lastName`, `email` unique, `phoneNumber`). Le conducteur a en plus un `drivingLicenseNumber` unique.
- **Car** : marque, modèle, immatriculation unique, couleur, nombre de places (`size`), appartient à un conducteur.
- **Trip** : conducteur, voiture, ville de départ/arrivée, date de départ, places proposées, statut, liste ordonnée de **StopPoint**.
- **StopPoint** : ville, ordre (`stopOrder`), heure de passage, prix depuis le départ.
- **Reservation** : passager, trajet, arrêt de montée, arrêt de descente, nombre de places, montant total, date, statut.

Statuts :
- `StatutTrajet` : `PUBLIE`, `COMPLET`, `TERMINE`, `ANNULE`
- `StatutReservation` : `CONFIRMEE`, `ANNULEE`

## Règles métier principales

**Trajets**
- La voiture doit appartenir au conducteur, et les places proposées ne peuvent pas dépasser sa capacité.
- Les points d'arrêt : pas de ville en double, ordres et heures strictement croissants, prix du premier arrêt = 0, prix strictement croissants.
- L'heure de départ doit être dans le futur.
- Seul un trajet `PUBLIE` ou `COMPLET` peut être terminé (après l'heure de départ) ou annulé, et uniquement par son conducteur.
- Un trajet `TERMINE` ou `ANNULE` ne peut plus être modifié ; un trajet ayant des réservations ne peut pas être supprimé.

**Réservations**
- Seuls les trajets `PUBLIE` peuvent être réservés.
- L'arrêt de montée doit précéder l'arrêt de descente.
- Un passager ne peut avoir qu'une seule réservation confirmée par trajet.
- Le nombre de places demandé ne doit pas dépasser les places disponibles ; le trajet passe à `COMPLET` quand il n'en reste plus.
- Prix = (prix arrêt de descente − prix arrêt de montée) × nombre de places.
- Annulation possible uniquement par le passager propriétaire, au moins **24 h avant le départ**.
- Le trajet est verrouillé (`findByIdForUpdate`) pendant les opérations concurrentes sur les places.

## Démarrage

### 1. Prérequis
- JDK 17+
- Docker & Docker Compose

### 2. Variables d'environnement

Créer un fichier `.env` à la racine :

```properties
POSTGRES_DB=covoiture_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432
```

Ce fichier est lu à la fois par `docker-compose.yml` et par Spring (`spring.config.import=optional:file:.env[.properties]`).

### 3. Lancer la base de données

```bash
docker compose up -d
```

### 4. Lancer l'application

```bash
./mvnw spring-boot:run        # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run      # Windows
```

L'API démarre sur `http://localhost:8080`. Le schéma est généré automatiquement (`ddl-auto=update`).

### 5. Tests

```bash
./mvnw test
```

## Documentation de l'API

Swagger UI : http://localhost:8080/swagger-ui.html
Spécification OpenAPI : http://localhost:8080/v3/api-docs

## Endpoints

### Conducteurs — `/api/drivers`
| Méthode | Route | Description |
|---|---|---|
| POST | `/api/drivers` | Créer un conducteur |
| GET | `/api/drivers` | Lister les conducteurs |
| GET | `/api/drivers/{id}` | Détails d'un conducteur |
| PUT | `/api/drivers/{id}` | Modifier un conducteur |
| GET | `/api/drivers/search?word=` | Rechercher des conducteurs |
| GET | `/api/drivers/{driverId}/trips` | Trajets d'un conducteur |

### Véhicules — `/api/vehicules`
| Méthode | Route | Description |
|---|---|---|
| POST | `/api/vehicules` | Ajouter un véhicule |
| GET | `/api/vehicules` | Lister les véhicules |
| GET | `/api/vehicules/{id}` | Détails d'un véhicule |
| PUT | `/api/vehicules/{id}` | Modifier un véhicule |
| DELETE | `/api/vehicules/{id}` | Supprimer un véhicule |
| GET | `/api/vehicules/drivers/{driverId}` | Véhicules d'un conducteur |

### Passagers — `/api/passagers`
| Méthode | Route | Description |
|---|---|---|
| POST | `/api/passagers` | Créer un passager |
| GET | `/api/passagers` | Lister les passagers |
| GET | `/api/passagers/{id}` | Détails d'un passager |
| PUT | `/api/passagers/{id}` | Modifier un passager |
| GET | `/api/passagers/search?email=` | Rechercher un passager par email |

### Trajets — `/api/trips`
| Méthode | Route | Description |
|---|---|---|
| POST | `/api/trips` | Publier un trajet (avec ses points d'arrêt) |
| GET | `/api/trips/{id}` | Détails d'un trajet |
| GET | `/api/trips/search` | Rechercher des trajets (paginé) |
| PATCH | `/api/trips/{id}/complete?driverId=` | Marquer un trajet comme terminé |
| PATCH | `/api/trips/{id}/cancel?driverId=` | Annuler un trajet |

Paramètres de recherche : `departureCity`, `arrivalCity`, `date` (format `YYYY-MM-DD`), `seats` (défaut 1), `maxPrice` (optionnel), `page` (défaut 0), `size` (défaut 10). La recherche fonctionne aussi sur un **segment** du trajet (villes intermédiaires).

```
GET /api/trips/search?departureCity=Casablanca&arrivalCity=Marrakech&date=2026-12-01&seats=2
```

### Points d'arrêt — `/api/stop-points`
| Méthode | Route | Description |
|---|---|---|
| GET | `/api/stop-points/trip/{tripId}` | Points d'arrêt d'un trajet |

### Réservations — `/api/reservations`
| Méthode | Route | Description |
|---|---|---|
| POST | `/api/reservations` | Réserver des places |
| GET | `/api/reservations/{id}?passagerId=` | Détails d'une réservation |
| GET | `/api/reservations/passager/{passagerId}` | Réservations d'un passager |
| GET | `/api/reservations/driver/{driverId}` | Réservations sur les trajets d'un conducteur |
| PATCH | `/api/reservations/{id}/cancel?passagerId=` | Annuler une réservation |

## Gestion des erreurs

Toutes les erreurs sont gérées par `GlobalExceptionHandler` et renvoient une réponse JSON uniforme :

| Exception | Code HTTP |
|---|---|
| `NotFoundException` | 404 Not Found |
| `InvalidRequestException`, erreurs de validation, paramètre manquant/invalide, JSON invalide | 400 Bad Request |
| `ForbiddenActionException` | 403 Forbidden |
| `ConflictException` | 409 Conflict |
| Autre exception | 500 Internal Server Error |
