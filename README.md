# Rate My Plate

A full-stack project consisting of an **Android application** (Kotlin) and a **Node/Express backend API** for browsing restaurants, rating dishes, and posting reviews. Owners can respond to reviews, and Firebase authentication is integrated for secure user and owner actions.

---

##  Table of Contents

- [Features](#-features)  
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Backend Setup (Node/Express)](#backend-api-setup-nodeexpress)
  - [Android Setup (Kotlin)](#android-app-setup-kotlin)
- [Configuration](#-configuration)
- [API Reference](#-api-reference)
- [Development & Branch Notes](#-development--branch-notes)
- [Scripts & Useful Commands](#-scripts--useful-commands)
- [License](#-license)

---

##  Features

### **Android App**
-  Browse nearby businesses  
- 🔎Search & filter restaurants by:
  - Name
  - Category
  - Minimum rating  
-  Write reviews, including:
  - Star rating  
  - Comment  
  - Optional image URL  
-  View reviews for each restaurant  
-  Firebase Authentication (email/password)  
-  Settings screen and logout  

### **Business/Owner Features**
- Create businesses  
- Respond to reviews (if authenticated as owner)  

### **Backend (Node/Express)**
- JSON file–based pseudo-database (`db.json`)  
- REST API for businesses and reviews  
- Optional Firebase Admin token verification  
- CORS enabled  
- Auto-seeds example data  

---

##  Architecture

```
Android App (Kotlin, Retrofit, Firebase)
        ⇅
Node.js REST API (Express + JSON DB)
```

**Shared Data Models**
- Business: id, name, category, rating, thumbnail, ownerId  
- Review: id, restaurantId, userId, rating, comment, imageUrl, ownerResponse  

---

##  Project Structure

```
.
├── app/                       # Android app module
├── build.gradle.kts
├── settings.gradle.kts
├── review-api/                # Node API backend
│   ├── server.js
│   ├── db.json
│   ├── package.json
│   └── package-lock.json
└── README.md
```

### Android Module (`app/`)
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/rate_my_plate/
│   │   │   ├── ui/
│   │   │   ├── data/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   └── res/
│   └── test/
└── build.gradle.kts
```

### Backend (`review-api/`)
```
review-api/
├── server.js
├── db.json
├── package.json
└── node_modules/
```

---

##  Getting Started

---

## Backend API Setup (Node/Express)

### 1. Install dependencies
```bash
cd review-api
npm install
```

### 2. Run the server
```bash
npm run dev
# or
npm start
```

### 3. Verify the backend
```bash
curl http://localhost:3000/businesses
```

### 4. Optional: Enable Firebase Admin
```bash
npm install firebase-admin
```
Provide service account credentials via environment variables.

---

## Android App Setup (Kotlin)

### 1. Open the project  
Open the root folder in **Android Studio**.

### 2. Add Firebase config  
Place your `google-services.json` inside:

```
app/google-services.json
```

### 3. Set API base URL  
In `ApiClient.kt`:

```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
```

For physical devices, replace with your machine’s LAN IP.

### 4. Run the app  

Firebase login → Business list screen → Reviews → Write a review.

---

##  Configuration

### Android Highlights
- Retrofit  
- OkHttp logging interceptor  
- Firebase Auth  
- Glide  
- Kotlin coroutines  

### Backend Highlights
- Express  
- CORS  
- Firebase Admin (optional)  
- JSON DB with auto-seed  

---

##  API Reference

Base URL examples:
- Emulator: `http://10.0.2.2:3000`
- Local machine: `http://localhost:3000`

---

### **Health**
```
GET /
GET /api/
```

---

### **Businesses**

#### GET /businesses
Query params:
- `q`
- `category`
- `rating`

#### POST /businesses
Creates a business.

```json
{
  "name": "New Café",
  "category": "Cafe",
  "description": "A cozy little place",
  "thumbnailUrl": "https://example.com/thumb.jpg"
}
```

---

### **Reviews**

#### GET /reviews?restaurantId=ID

#### POST /reviews

```json
{
  "restaurantId": "1",
  "userId": "someUid",
  "rating": 4.5,
  "comment": "Great food!",
  "imageUrl": "https://example.com/review.jpg"
}
```

---

### **Owner Responses**

#### PATCH /reviews/:id
```json
{
  "ownerResponse": "Thank you!"
}
```

---

##  Development & Branch Notes

This README reflects the merged contents of:

- `main`
- `TesharPOEP3`
- `Justeen`
- `review-api-main`

All combined into one unified Android + API project.

---

##  Scripts & Useful Commands

### Backend
```bash
npm install
npm run dev
npm start
```

### Android
```bash
./gradlew test
./gradlew connectedAndroidTest
./gradlew assembleDebug
```




