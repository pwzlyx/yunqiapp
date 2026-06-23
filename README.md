# Yunqi Pregnancy Android App

Yunqi is a local-first Android MVP for pregnancy planning and daily tracking. It helps pregnant users calculate gestational age, view week-aware guidance, record calendar events, manage reminders, and review local trends without sending health data to a cloud service.

## Current Status

- Branch: `feature/android-scaffold`
- Remote: `https://github.com/pwzlyx/yunqiapp.git`
- Android app: Kotlin, Jetpack Compose, Material 3, Room, DataStore, WorkManager
- Storage policy: local-only MVP storage; Android cloud backup and device-transfer extraction are disabled
- Automated gates: debug APK build, androidTest APK build, JVM tests, and Android lint are passing
- Runtime gap: physical-device/emulator notification delivery still needs verification on a device that can run the APK

## Product Documents

- [Pregnancy App PRD](docs/PRD_PREGNANCY_APP.md)
- [Development Plan](docs/DEVELOPMENT_PLAN.md)
- [Android Environment Setup](docs/ANDROID_ENV_SETUP.md)
- [MVP Review Checklist](docs/MVP_REVIEW_CHECKLIST.md)
- [Release Validation](docs/RELEASE_VALIDATION.md)
- [GitHub Setup](docs/GITHUB_SETUP.md)

## Implemented MVP

- Pregnancy setup from last menstrual period, due date, conception date, or current gestational age
- Home dashboard with current gestational age, due-date countdown, local reminders, diet, exercise, antenatal care, and safety guidance
- Calendar month view with local records for appointments, weight, fetal movement, symptoms, exercise, diet, and notes
- Edit, delete, validation, and confirmation flows for local calendar records
- Appointment reminders and daily reminders through local WorkManager jobs and Android notifications
- Trends for weight, fetal movement, exercise, and upcoming appointment plans
- Settings for pregnancy profile editing, reminder preferences, notification permission guidance, CSV export, profile deletion, and full local data clearing
- Traceable local guidance content with source URLs, review dates, risk levels, and locale metadata
- Unit and instrumentation test coverage for core calculations, database migrations, reminders, export safety, content coverage, and key Compose navigation flows

## Open In Android Studio

Android Studio is installed locally at:

```text
C:\Users\zengzeng\Documents\Codex\AndroidStudio\android-studio\bin\studio64.exe
```

Open this project folder:

```text
C:\Users\zengzeng\Documents\Codex\2026-06-21\w\android-app
```

Let Android Studio sync Gradle, then run the `app` configuration on a physical Android device or a working emulator.

## Command-Line Build

Run from `android-app`:

```powershell
$env:JAVA_HOME=(Resolve-Path .\..\tools\jdk\jdk-17* | Select-Object -First 1).Path
$env:ANDROID_HOME=(Resolve-Path .\..\tools\android-sdk).Path
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
$env:Path="$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:Path"
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --stacktrace
```

Generated APKs:

```text
app\build\outputs\apk\debug\app-debug.apk
app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk
```

See [Release Validation](docs/RELEASE_VALIDATION.md) for manual smoke-test steps and the current emulator verification blocker.
