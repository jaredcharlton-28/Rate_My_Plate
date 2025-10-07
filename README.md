# Rate My Plate 

An Android app (Kotlin) for sharing and rating dishes. Snap a photo, jot a review, and help others discover their next favorite plate.


---

## Features

- Rate dishes with stars and short reviews
- Attach photos of plates you’ve tried
- Browse recent and top-rated stores
- Search/filter by restaurant, tag, or rating
- Sign-in for syncing favorites & reviews


---


## Getting started

### Prerequisites
- Android Studio (Giraffe+ recommended)
- JDK 17 (use Android Studio’s bundled JDK if possible)
- Android SDK with latest stable compileSdk and build tools

### Clone
```bash
git clone https://github.com/jaredcharlton-28/Rate_My_Plate.git
cd Rate_My_Plate
```

### Open
- Open the project in Android Studio (use the top-level build.gradle.kts).
- Let Gradle sync and download dependencies.

---

## Build & run

### From Android Studio
- Select a device/emulator.
- Click Run ▶️.

### From the command line
```bash
# Debug build
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug

# Release build (configure signing first)
./gradlew assembleRelease
```

---

## Project structure

```
Rate_My_Plate/
├─ app/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ AndroidManifest.xml
│  │  │  ├─ java/...              # Kotlin source
│  │  │  └─ res/...               # Layouts, drawables, strings
│  │  └─ test/ ...                # Unit tests
│  └─ build.gradle.kts
├─ gradle/ ...                    # Gradle wrapper files
├─ build.gradle.kts               # Root Gradle config
├─ settings.gradle.kts            # Module inclusion
└─ gradle.properties              # JVM/Gradle settings
```


## Configuration

Create or update these files as needed:

- local.properties — SDK paths (managed by Android Studio)
- gradle.properties — JVM/Gradle flags
- app/src/main/res/values/strings.xml — app name & strings
- BuildConfig fields — API keys or endpoints 

---
