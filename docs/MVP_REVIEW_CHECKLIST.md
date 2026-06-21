# Yunqi Android MVP Review Checklist

## Automated Gates

Run from `android-app` with JDK 17 and Android SDK configured:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --stacktrace
```

Current result: passing.

## Implemented MVP Evidence

- Pregnancy setup supports last menstrual period, due date, and current gestational age.
- Home reads the local pregnancy profile and displays current gestational age, due date countdown, reminders, diet guidance, exercise guidance, safety guidance, source names, source URLs, and review dates.
- Calendar supports month view, date markers, day detail list, create, edit, and delete with confirmation.
- Calendar record types include appointment, weight, fetal movement, symptom, exercise, diet, and note.
- Appointment reminders use local WorkManager jobs, Android notifications, a notification channel, notification permission handling, and a settings toggle.
- Trends show weight, fetal movement, and exercise charts from local Room records.
- Settings can edit pregnancy profile, manage appointment reminders, and clear all local pregnancy data.
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
- Compose UI instrumentation tests are implemented and can be built with `:app:assembleDebugAndroidTest`, but have not been executed on a physical device inside this workspace.
