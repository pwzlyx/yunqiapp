# Android App Workspace

This workspace is reserved for the Android app project.

## Project Status

- Workspace created: 2026-06-21
- Git installed: `tools/git` portable Git for Windows
- Git initialized: yes, branch `main`
- GitHub remote: `https://github.com/pwzlyx/yunqiapp.git`
- Product requirements: `docs/PRD_PREGNANCY_APP.md`
- Development plan: `docs/DEVELOPMENT_PLAN.md`
- Android project scaffold: Compose app skeleton on `feature/android-scaffold`
- Data policy: local-only MVP storage, Android backup disabled

## Documents

- [Pregnancy App PRD](docs/PRD_PREGNANCY_APP.md)
- [Development Plan](docs/DEVELOPMENT_PLAN.md)
- [GitHub Setup](docs/GITHUB_SETUP.md)

## Recommended Next Steps

1. Configure Git identity.
2. Create or provide a GitHub repository URL.
3. Connect this folder to GitHub.
4. Install Android Studio.
5. Scaffold the Android app project inside this folder.

## Planned Structure

```text
android-app/
  app/                 Android application module
  docs/                Project notes and setup docs
  README.md            Project overview
  .gitignore           Git ignore rules
```

## Android Scaffold

The current scaffold includes:

- Kotlin Android app module
- Jetpack Compose and Material 3
- Bottom navigation: Home, Calendar, Trends, Settings
- Initial Yunqi theme
- Pregnancy date calculation domain class
- Unit test skeleton for pregnancy calculation
- Local-only privacy defaults for pregnancy records

Open this folder in Android Studio:

```text
C:\Users\zengzeng\Documents\Codex\2026-06-21\w\android-app
```

Build command after JDK 17+ and Android SDK are installed:

```powershell
.\gradlew.bat :app:assembleDebug
```
