# Android Environment Setup

This project is scaffolded as a native Android app using Kotlin and Jetpack Compose.

## Required Local Tools

- Android Studio
- JDK 17 or newer
- Android SDK Platform 35

This workspace now includes local command-line build tools:

- JDK: `C:\Users\zengzeng\Documents\Codex\2026-06-21\w\tools\jdk`
- Android SDK: `C:\Users\zengzeng\Documents\Codex\2026-06-21\w\tools\android-sdk`

The user environment variables `JAVA_HOME`, `ANDROID_HOME`, and `ANDROID_SDK_ROOT` have been configured for future terminals.

## Local-Only Data Policy

The MVP stores pregnancy information on the device only. Android cloud backup and device-transfer extraction are disabled by default because pregnancy profiles, weight, fetal movement, appointments, and notes are sensitive health data.

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

The first successful debug APK is generated at:

```text
app\build\outputs\apk\debug\app-debug.apk
```
