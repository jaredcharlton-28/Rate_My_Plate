# Rate My Plate

Rate My Plate is an Android app (Kotlin) for sharing, reviewing, and rating dishes from restaurants. Snap a photo, write a review, browse popular plates, and help others discover their next favorite meal.

The project includes:
- A full Android application built using modern development standards.
- A lightweight Node.js JSON API for storing reviews, restaurants, and user data.

---

## Features

### Core App Features
- Rate dishes using a 1–5 star system
- Write detailed reviews with photos
- Browse recent or top-rated dishes
- Explore restaurants and items
- Search/filter by restaurant, tag, rating, or dish type

### Authentication
- **Firebase Authentication**
  - Email & Password
  - Google Sign-In (optional)
  - Secure session persistence
- **Biometric Sign-In**
  - Fingerprint
  - Face Unlock
  - Device PIN/Pattern fallback

###  Media & Storage
- Capture photos using the camera
- Select photos from the gallery
- Image preview before posting
- Efficient caching (Coil/Glide)

###  Backend Integration
- Lightweight Node.js API
- JSON-based database (`db.json`)
- Hot reload via Nodemon
- Endpoints for reviews, stores, users

---

## Tech Stack

### Android App
- Kotlin
- MVVM Architecture
- Jetpack Compose / XML
- Retrofit + OkHttp + Coroutines
- Hilt (dependency injection)
- Coil / Glide (image loading)
- DataStore / Room (local storage)
- Firebase Auth
- AndroidX Biometric API

### Backend API
- Node.js
- JSON Server
- Nodemon
- db.json for data persistence

---

## Getting Started

### Prerequisites

#### Android
- Android Studio **Giraffe+**
- JDK **17**
- Latest `compileSdk` + build tools

#### Backend
- Node.js **18+**
- npm or yarn

---

## Clone the Repository

```bash
git clone https://github.com/jaredcharlton-28/Rate_My_Plate.git
cd Rate_My_Plate
```

---

## Opening the Project (Android)

1. Open **Android Studio**
2. Select: **Open an Existing Project**
3. Choose the project root
4. Let Gradle sync automatically
5. Add your `google-services.json` into `app/google-services.json`

---

## Biometric Sign-In

The app supports:
- Fingerprint
- Face Unlock
- Device Credentials

No additional setup required.

---

## Configure API URL

Set the API base URL:

```kotlin
buildConfigField("String", "BASE_URL", ""http://10.0.2.2:3000/"")
```

For real devices, replace with your computer’s LAN IP.

---

## Build & Run

### Android Studio
- Select a device or emulator
- Press **Run ▶️**

### Command Line

```bash
./gradlew assembleDebug
./gradlew installDebug
./gradlew assembleRelease
```

---

## Backend API Setup

```bash
cd review-api
npm install
npm start
```

Server runs at:

```
http://localhost:3000/
```

---

## API Endpoints

### Reviews
```
GET    /reviews
POST   /reviews
GET    /reviews/:id
PATCH  /reviews/:id
DELETE /reviews/:id
```

### Stores
```
GET    /stores
POST   /stores
```

### Users
```
GET    /users
POST   /users
```

---

## Example Review JSON

```json
{
  "id": 12,
  "rating": 5,
  "comment": "Amazing sushi, super fresh!",
  "photoUrl": "/images/sushi.jpg",
  "storeId": 3,
  "userId": 4,
  "timestamp": "2025-02-12T18:00:00Z"
}
```

---

## Project Structure

```
Rate_My_Plate/
├─ app/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ AndroidManifest.xml
│  │  │  ├─ java/...       # ViewModels, UI, Repositories
│  │  │  └─ res/...        # Layouts, images, strings
│  │  └─ test/...          # Unit tests
│  └─ build.gradle.kts
│
├─ review-api/
│  ├─ db.json              # Mock data storage
│  ├─ server.js            # JSON server setup
│  └─ package.json
│
├─ gradle/
├─ build.gradle.kts
├─ settings.gradle.kts
└─ gradle.properties
```

---

## Configuration Notes

- **local.properties** → SDK paths
- **gradle.properties** → JVM/Gradle flags
- **strings.xml** → UI text
- **BuildConfig** → API endpoints, keys

---

## Architecture

### MVVM Breakdown

**View Layer (Compose/XML)**  
- Renders UI  
- Observes state  

**ViewModel Layer**  
- Holds UI state  
- Business logic  
- Repository communication  

**Repository Layer**  
- API calls  
- Firebase auth  
- Local persistence  

---

## Security Notes
- Firebase handles credentials securely
- No raw passwords stored in the app
- Biometric data is device-controlled
- HTTPS recommended for production
- JSON Server is dev/testing only

---

## Testing

### App Testing
- JUnit
- Espresso / Compose Test

### API Testing
```bash
curl http://localhost:3000/reviews
```

---
