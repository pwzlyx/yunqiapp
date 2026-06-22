# Yunqi Android MVP Review Checklist

## Automated Gates

Run from `android-app` with JDK 17 and Android SDK configured:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --stacktrace
```

Current result: passing.

## Implemented MVP Evidence

- Pregnancy setup supports last menstrual period, due date, conception date, and current gestational age.
- Pregnancy setup supports optional height, pre-pregnancy weight, and singleton or twin profile details.
- Pregnancy setup pre-fills the saved local profile when editing from Settings, including current gestational age recalculated for the edit date.
- Pregnancy setup supports marking doctor-restricted exercise; Home suppresses default exercise suggestions when enabled.
- Home reads the local pregnancy profile and displays current gestational age, due date countdown, reminders, quick record entry points for weight, fetal movement, symptoms, and appointments, diet guidance, antenatal care guidance, exercise guidance, safety guidance, source names, source URLs, and review dates.
- Diet guidance cards can be marked as read or favorited with local-only state.
- Calendar supports month view, date markers, day detail list, create, edit, and delete with confirmation.
- Calendar record types include appointment, weight, fetal movement, symptom, exercise, diet, and note.
- Fetal movement records include count, time period, feeling, and note.
- Symptom, exercise, and diet records include PRD-specific structured fields in addition to notes.
- Calendar shows a medical-attention notice when recording fetal movement or symptoms.
- Appointment records include time, hospital or location, doctor, check items, result, and note.
- Appointment time input is validated as `HH:mm` before reminder scheduling.
- Appointment reminders use local WorkManager jobs, Android notifications, a notification channel, notification permission handling, and a settings toggle.
- Daily reminders support weight, fetal movement, folic acid or vitamin, water, exercise, and custom reminder types with independent local WorkManager jobs, Android notifications, notification permission handling, toggles, and configurable preset times.
- Trends show weight, fetal movement, and exercise charts from local Room records.
- Trends show charts only when a metric has at least two comparable records.
- Trends show locally recorded appointment plans with date, time, location, doctor, and check items.
- Trends support 7-day, 30-day, and all-time ranges.
- Trends empty states provide direct entry points for recording weight, fetal movement, and exercise.
- Settings can edit pregnancy profile, manage appointment reminders, and clear all local pregnancy data.
- Settings can export local calendar records as a CSV file through the Android share sheet.
- Sensitive data stays local: cloud backup and device transfer extraction are disabled.

## Manual Smoke Test

Use a physical Android device or emulator:

1. Install `app/build/outputs/apk/debug/app-debug.apk`.
2. Open the app and create a pregnancy profile using each supported setup method.
3. Verify Home updates gestational age and advice after saving a profile.
4. Create appointment, weight, fetal movement, symptom, exercise, diet, and note records from Calendar.
5. Edit one record and confirm it updates rather than duplicating.
6. Delete one record and confirm the delete dialog appears before removal.
7. Enable appointment reminders from Settings and grant notification permission on Android 13+.
8. Create a future appointment with `HH:mm` time and verify a local notification is scheduled.
9. Disable reminders and verify pending appointment reminders no longer fire.
10. Add at least two weight and fetal movement records, plus one exercise record, and verify Trends charts update.
11. Turn off network access and verify Home, Calendar, Trends, and Settings still work.
12. Use Settings to clear all local data and verify Home returns to the missing-profile state and Calendar records disappear.

## Known Verification Gap

- Physical-device notification delivery has not been observed inside this workspace.
- `adb devices` currently reports no attached device.
- Android Emulator and Android 35 x86_64/ARM64 system images are installed locally, but the x86_64 AVD cannot boot because Android Emulator Hypervisor Driver is not installed, and the ARM64 AVD is not supported on this x86_64 host.
- Compose UI instrumentation tests are implemented and can be built with `:app:assembleDebugAndroidTest`, but have not been executed on a physical device inside this workspace.
