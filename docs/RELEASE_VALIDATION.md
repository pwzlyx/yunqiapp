# Release Validation

This document records the repeatable MVP validation flow for Yunqi before sharing a debug APK for manual testing.

## Automated Gate

Run from `android-app`:

```powershell
.\scripts\validate_mvp.ps1
```

The validation script configures the local JDK and Android SDK paths, runs the debug build, androidTest APK build, JVM tests, and lint, verifies that both APK artifacts exist, and reports whether `adb` has a connected device.

The Gradle gate must pass before the APK is considered ready for manual smoke testing.

Generated artifacts:

```text
app\build\outputs\apk\debug\app-debug.apk
app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk
```

## Manual Smoke Test Prerequisites

Use one of the following:

- A physical Android device with USB debugging enabled.
- A Windows emulator that can boot successfully with hardware acceleration enabled.

Confirm device availability:

```powershell
adb devices
```

At least one device should be listed as `device` before installing the APK or running connected tests.

## Install And Launch

Install the debug APK:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell monkey -p com.yunqi.app 1
```

Run connected Compose instrumentation tests when a device is available:

```powershell
.\scripts\validate_mvp.ps1 -RunConnectedTests
```

## Manual MVP Checklist

1. Create a pregnancy profile with each supported setup method: last menstrual period, due date, conception date, and current gestational age.
2. Verify Home updates gestational age, due-date countdown, guidance cards, medical disclaimer, and urgent-care notice after saving.
3. Create appointment, weight, fetal movement, symptom, exercise, diet, and note records from Calendar.
4. Edit one record and confirm it updates the original entry rather than duplicating it.
5. Delete one record and confirm the delete dialog appears before removal.
6. Enable appointment reminders from Settings and grant notification permission on Android 13+.
7. Create a future appointment with a strict `HH:mm` time and verify a local notification is scheduled.
8. Disable appointment reminders and verify stale pending work is cancelled.
9. Enable each daily reminder type, customize its time, and verify Home reflects the normalized reminder schedule.
10. Add at least two weight records, two fetal movement records, and one exercise record; verify Trends charts and upcoming appointment plans update.
11. Turn off network access and verify Home, Calendar, Trends, Settings, reminders, and local guidance remain usable.
12. Export CSV from Settings and verify Chinese text opens cleanly in a spreadsheet app.
13. Delete only the pregnancy profile and verify calendar records are preserved while reminders are disabled.
14. Clear all local data and verify Home returns to the missing-profile state, Calendar records disappear, CSV cache is removed, and reminders are disabled.

## Current Runtime Verification Blocker

The command-line build and local JVM/lint gates pass, but device-level runtime verification is still pending inside this workspace.

Observed state:

- `adb devices` reports no attached physical device.
- Android Emulator and Android 35 x86_64/ARM64 system images are installed locally.
- A `YunqiApi35` x86_64 AVD exists, but boot fails because Android Emulator Hypervisor Driver is not installed on this Windows host.
- `sc.exe query aehd` reports service 1060 / not installed.
- The Android Emulator Hypervisor Driver `silent_install_safe.bat` installer was attempted and hung during driver installation; the stuck installer processes were stopped.
- The ARM64 AVD is not suitable for this x86_64 host.

After installing the emulator hypervisor driver with administrator permissions, verify acceleration and boot the AVD:

```powershell
emulator -accel-check
emulator -avd YunqiApi35 -no-audio -no-boot-anim -gpu swiftshader_indirect -no-snapshot
adb shell getprop sys.boot_completed
```

When `sys.boot_completed` returns `1`, rerun APK installation and `:app:connectedDebugAndroidTest`.
