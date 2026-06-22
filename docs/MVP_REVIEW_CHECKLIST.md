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
- Pregnancy setup rejects current gestational age entries beyond 42 weeks.
- Pregnancy setup rejects unrealistic height and pre-pregnancy weight values before they can distort BMI guidance.
- Pregnancy setup trims numeric gestational age inputs before validation.
- Pregnancy setup supports marking doctor-restricted exercise; Home suppresses default exercise suggestions when enabled.
- Pregnancy setup preview warns users to confirm dates when the calculation is already beyond 42 weeks.
- Home avoids negative due-date countdowns and shows a doctor-judgment reminder on or after the due date.
- Home reads the local pregnancy profile and displays current gestational age, due date countdown, reminders, quick record entry points for weight, fetal movement, symptoms, notes, and appointments, diet guidance, antenatal care guidance, exercise guidance, safety guidance, source names, source URLs, and review dates.
- Home shows the medical disclaimer and urgent medical-attention reminder even before a pregnancy profile is created.
- Home shows today's reminder list from local daily reminder settings and today's appointment records.
- Home keeps today's appointment records visible even when system appointment notifications are disabled.
- Home shows the user-entered custom reminder message in today's reminder list.
- Diet guidance cards can be marked as read or favorited with local-only state.
- Exercise guidance can be hidden and restored with local-only state.
- Calendar supports month view, date markers, day detail list, create, edit, and delete with confirmation.
- Calendar record types include appointment, weight, fetal movement, symptom, exercise, diet, and note.
- Calendar record database mapping preserves all structured appointment, fetal movement, symptom, exercise, and diet fields.
- Calendar record database migration SQL is covered by JVM tests for all structured fields added after version 1.
- Calendar rejects unrealistic weight, fetal movement count, and exercise duration values before they can distort local trends.
- Calendar trims numeric weight, fetal movement, and exercise inputs before validation.
- Fetal movement records include count, time period, feeling, and note.
- Symptom, exercise, and diet records include PRD-specific structured fields in addition to notes.
- Calendar shows a medical-attention notice when recording and reviewing fetal movement or symptom records.
- Appointment records include time, hospital or location, doctor, check items, result, and note.
- Appointment time input is validated as `HH:mm` before reminder scheduling.
- Appointment reminders use local WorkManager jobs, Android notifications, a notification channel, notification permission handling, and a settings toggle.
- Appointment reminders support configurable lead times of 1 hour, 6 hours, or 1 day, and changing the lead time rebuilds future appointment reminder work.
- Appointment reminder notifications open the app when tapped.
- Future appointments inside the configured lead window schedule an immediate local reminder instead of silently dropping the notification.
- Editing an appointment so it no longer has a valid future reminder cancels stale scheduled reminder work for that record.
- Daily reminders support weight, fetal movement, folic acid or vitamin, water, exercise, and custom reminder types with independent local WorkManager jobs, Android notifications, notification permission handling, toggles, and configurable preset times.
- Daily reminder notifications open the app when tapped.
- Custom daily reminders support a user-entered local message that is delivered in the notification body.
- Daily reminders cancel stale scheduled work when a stored reminder time is invalid instead of leaving an old notification active.
- Daily and appointment reminders are rebuilt from local settings after device boot or app package replacement.
- Settings and reminder workers check both Android 13 notification runtime permission and the app-level notification switch.
- Settings shows separate guidance for missing runtime notification permission and disabled app-level notifications.
- Settings shows notification-permission guidance and a system settings entry point when Android notifications are disabled.
- Settings enables the requested reminder from the latest notification permission state after the Android 13 permission prompt returns.
- Settings preserves saved reminder preferences when Android notifications are temporarily unavailable.
- Settings rebuilds future appointment reminder work when Android notifications become available again and appointment reminders are still enabled.
- Trends show weight, fetal movement, and exercise charts from local Room records.
- Trends show CDC-sourced pregnancy weight-gain guidance when the local profile includes height, pre-pregnancy weight, and singleton or twin status.
- Trends show charts only when a metric has at least two comparable records.
- Trends keep upcoming appointment plans visible from all local records while metric charts follow the selected 7-day, 30-day, or all-time range.
- Trends show locally recorded appointment plans with date, time, location, doctor, and check items.
- Trends support 7-day, 30-day, and all-time ranges.
- Trends empty states provide direct entry points for recording weight, fetal movement, and exercise.
- Settings can edit pregnancy profile, manage appointment reminders, and clear all local pregnancy data.
- Settings can delete only the pregnancy profile after confirmation, while preserving calendar records and disabling local reminders.
- Settings can export local calendar records as a CSV file through the Android share sheet.
- CSV exports grant read access through both `EXTRA_STREAM` and `ClipData` for broad Android share target compatibility.
- CSV exports include a UTF-8 BOM so Chinese text opens cleanly in common spreadsheet apps.
- Settings shows local-data privacy guidance, the medical disclaimer, and urgent medical-attention reminder.
- CSV export protects user-entered cells from being interpreted as spreadsheet formulas.
- Clearing local data also removes cached CSV export files.
- Reminder restore failures use sanitized logs that omit exception details which could contain local health records.
- Sensitive data stays local: cloud backup and device transfer extraction are disabled.
- Compose instrumentation smoke tests cover core tab navigation, medical notices, and pregnancy setup due-date preview.

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
