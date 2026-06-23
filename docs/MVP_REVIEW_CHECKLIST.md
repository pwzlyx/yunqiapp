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
- Pregnancy setup rejects future last menstrual period or conception dates, and due dates that imply a future last menstrual period.
- Pregnancy setup rejects current gestational age entries beyond 42 weeks.
- Pregnancy setup rejects unrealistic height and pre-pregnancy weight values before they can distort BMI guidance.
- Pregnancy setup trims numeric gestational age inputs before validation.
- Pregnancy setup date inputs use an ISO-date keyboard that supports entering hyphens.
- Pregnancy setup supports marking doctor-restricted exercise; Home suppresses default exercise suggestions when enabled.
- Pregnancy progress tests cover trimester boundary weeks and post-due countdown handling.
- Pregnancy setup preview warns users to confirm dates when the calculation is already beyond 42 weeks.
- Locally stored pregnancy profile dates are parsed defensively so corrupt stored values fall back to the missing-profile state instead of crashing Home or Trends.
- Locally stored pregnancy profiles require method-specific calculation fields before Home or Trends can calculate progress, so partially corrupt stored profiles fall back to the missing-profile state.
- Locally stored optional height and pre-pregnancy weight values are range-filtered before BMI guidance uses them.
- Home avoids negative due-date countdowns and shows a doctor-judgment reminder on or after the due date.
- Home reads the local pregnancy profile and displays current gestational age, due date countdown, reminders, quick record entry points for weight, fetal movement, symptoms, notes, and appointments, diet guidance, antenatal care guidance, exercise guidance, safety guidance, source names, source URLs, and review dates.
- Home quick record entry points also cover diet and exercise so daily guidance can be recorded without first browsing Calendar record types.
- Home shows the medical disclaimer and urgent medical-attention reminder even before a pregnancy profile is created.
- Home shows today's reminder list from local daily reminder settings and today's appointment records.
- Home keeps today's appointment records visible even when system appointment notifications are disabled.
- Home shows the user-entered custom reminder message in today's reminder list.
- Local content cards include traceable source URLs, review dates, risk levels, and locale metadata.
- Local content coverage is tested for every supported pregnancy week from 0 through 42 across diet, exercise, antenatal care, and safety categories.
- Diet guidance body copy is tested to avoid medical promise terms such as guarantee, cure, or diagnosis.
- Diet guidance cards can be marked as read or favorited with local-only state.
- Exercise guidance can be hidden and restored with local-only state.
- Second and third trimester exercise guidance is tested to avoid supine-position recommendations.
- Exercise guidance body copy includes a weekly activity goal and a pre-activity self-check reminder.
- Local content read, favorite, and hidden status ignores blank content IDs before persisting state.
- Exercise guidance visibility is covered with real local content IDs so hidden cards stay restorable.
- Calendar supports month view, date markers, day detail list, create, edit, and delete with confirmation.
- Calendar supports filtering the selected day's records by record type while preserving the sorted list order.
- Calendar quick-record navigation starts a fresh form for the requested record type instead of carrying stale draft or edit state across entry points.
- Calendar record types include appointment, weight, fetal movement, symptom, exercise, diet, and note.
- Calendar record database mapping preserves all structured appointment, fetal movement, symptom, exercise, and diet fields.
- Calendar record database migration SQL is covered by JVM tests for all structured fields added after version 1.
- Calendar record reads skip corrupt stored dates or unknown legacy types instead of crashing local views, export, or reminder rebuilds.
- Calendar record reads sanitize legacy free-text fields before they reach local views, export, or reminder rebuilds.
- Calendar rejects unrealistic weight, fetal movement count, and exercise duration values before they can distort local trends.
- Calendar rejects empty symptom, diet, and note records before they can clutter local history or CSV exports.
- Calendar trims numeric weight, fetal movement, and exercise inputs before validation.
- Calendar trims and length-caps free-text record fields before local persistence and CSV export.
- Calendar date input uses an ISO-date keyboard that supports entering hyphens.
- Fetal movement records include count, time period, feeling, and note, and can be saved with either a count or a feeling.
- Symptom, exercise, and diet records include PRD-specific structured fields in addition to notes.
- Calendar shows a medical-attention notice when recording and reviewing fetal movement or symptom records.
- Appointment records include time, hospital or location, doctor, check items, result, and note.
- Calendar rejects empty appointment records before they can clutter local history or produce unusable reminders.
- Appointment time input is validated as `HH:mm` before reminder scheduling.
- Appointment and daily reminder scheduling reject ISO times with seconds so every reminder path uses strict `HH:mm`.
- Strict `HH:mm` parsing is centralized in a shared time utility and covered by JVM tests to keep setup, settings, and reminder scheduling aligned.
- Appointment reminders use local WorkManager jobs, Android notifications, a notification channel, notification permission handling, and a settings toggle.
- Appointment reminders support configurable lead times of 1 hour, 6 hours, or 1 day, and changing the lead time rebuilds future appointment reminder work.
- Appointment reminder lead times are normalized to the supported 1 hour, 6 hours, or 1 day options before Home display or WorkManager sync.
- Appointment reminder notifications open the app when tapped.
- Appointment reminder notification labels include trimmed appointment time, location, doctor, and check items when available.
- Appointment reminder notification IDs are stable and non-negative, including the `Int.MIN_VALUE` hash edge case.
- Future appointments inside the configured lead window schedule an immediate local reminder instead of silently dropping the notification.
- Editing an appointment so it no longer has a valid future reminder cancels stale scheduled reminder work for that record.
- Daily reminders support weight, fetal movement, folic acid or vitamin, water, exercise, and custom reminder types with independent local WorkManager jobs, Android notifications, notification permission handling, toggles, and configurable preset times.
- Daily reminder notifications open the app when tapped.
- Custom daily reminders support a user-entered local message that is delivered in the notification body.
- Custom daily reminder messages are trimmed and length-capped before DataStore persistence, WorkManager scheduling, and notification display.
- Daily reminders cancel stale scheduled work when a stored reminder time is invalid instead of leaving an old notification active.
- Daily reminder settings normalize invalid or legacy stored reminder times back to valid per-type defaults before Home display or WorkManager sync.
- Settings uses normalized daily reminder times and appointment lead times for immediate WorkManager scheduling, not only persisted preferences.
- Daily and appointment reminders are rebuilt from local settings after device boot or app package replacement.
- Settings and reminder workers check both Android 13 notification runtime permission and the app-level notification switch.
- Settings shows separate guidance for missing runtime notification permission and disabled app-level notifications.
- Settings shows notification-permission guidance and a system settings entry point when Android notifications are disabled.
- Settings enables the requested reminder from the latest notification permission state after the Android 13 permission prompt returns.
- Settings preserves saved reminder preferences when Android notifications are temporarily unavailable.
- Settings rebuilds future appointment reminder work when Android notifications become available again and appointment reminders are still enabled.
- Trends show weight, fetal movement, and exercise charts from local Room records.
- Trends ignore legacy metric records outside the supported weight, fetal movement, and exercise ranges before chart aggregation.
- Trends aggregate same-day metric records into daily chart points: latest weight, total fetal movement, and total exercise minutes.
- Trends show CDC-sourced pregnancy weight-gain guidance when the local profile includes height, pre-pregnancy weight, and singleton or twin status.
- Trends show charts only when a metric has at least two comparable records.
- Trends keep upcoming appointment plans visible from all local records while metric charts follow the selected 7-day, 30-day, or all-time range.
- Trends exclude future-dated metric records from weight, fetal movement, and exercise charts while still showing future appointment plans separately.
- Trends show locally recorded appointment plans with date, time, location, doctor, and check items.
- Trends support 7-day, 30-day, and all-time ranges.
- Trends empty states provide direct entry points for recording weight, fetal movement, and exercise.
- Settings can edit pregnancy profile, manage appointment reminders, and clear all local pregnancy data.
- Settings can delete only the pregnancy profile after confirmation, while preserving calendar records and disabling local reminders.
- Settings can export local calendar records as a CSV file through the Android share sheet.
- CSV exports grant read access through both `EXTRA_STREAM` and `ClipData` for broad Android share target compatibility.
- CSV exports include a UTF-8 BOM so Chinese text opens cleanly in common spreadsheet apps.
- CSV export preparation failures show a generic local error instead of crashing or exposing exception details.
- Settings shows local-data privacy guidance, the medical disclaimer, and urgent medical-attention reminder.
- CSV export protects user-entered cells from being interpreted as spreadsheet formulas.
- Clearing local data also removes cached CSV export files.
- Reminder restore failures use sanitized logs that omit exception details which could contain local health records.
- Sensitive data stays local: cloud backup and device transfer extraction are disabled.
- Compose instrumentation smoke tests cover core tab navigation, medical notices, pregnancy setup due-date preview, and the diet quick-record path into Calendar.
- Compose instrumentation smoke tests reset local pregnancy data before each run to avoid device-state leakage.

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
- Android Emulator and Android 35 x86_64/ARM64 system images are installed locally.
- A `YunqiApi35` x86_64 AVD was created for runtime verification, but it cannot boot because Android Emulator Hypervisor Driver is not installed on this Windows machine.
- `silent_install_safe.bat` for Android Emulator Hypervisor Driver was attempted and hung in the driver install step; the stuck `rundll32` and `cmd` installer processes were stopped, and `sc.exe query aehd` still reports service 1060 / not installed.
- The ARM64 AVD remains unsupported on this x86_64 host.
- Compose UI instrumentation tests are implemented and can be built with `:app:assembleDebugAndroidTest`, but have not been executed on a physical device inside this workspace.
