# Android Environment Setup

This project is scaffolded as a native Android app using Kotlin and Jetpack Compose.

## Required Local Tools

- Android Studio
- JDK 17 or newer
- Android SDK Platform 35

The current Codex execution environment only has Java 8 available and does not have Android SDK configured, so full Gradle build verification needs a local Android development environment.

## Open The Project

1. Open Android Studio.
2. Select this folder:

```text
C:\Users\zengzeng\Documents\Codex\2026-06-21\w\android-app
```

3. Let Android Studio sync Gradle.
4. Install any SDK components prompted by Android Studio.
5. Run the `app` configuration on an emulator or Android phone.

## First Build Command

After JDK 17 and Android SDK are ready:

```powershell
.\gradlew.bat :app:assembleDebug
```

If the Gradle wrapper JAR is missing, create it from Android Studio or run `gradle wrapper` with a local Gradle installation.

