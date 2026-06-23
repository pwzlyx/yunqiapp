param(
    [switch]$RunConnectedTests
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $ProjectRoot

$JdkPath = (Resolve-Path ".\..\tools\jdk\jdk-17*" | Select-Object -First 1).Path
$AndroidSdkPath = (Resolve-Path ".\..\tools\android-sdk").Path

$env:JAVA_HOME = $JdkPath
$env:ANDROID_HOME = $AndroidSdkPath
$env:ANDROID_SDK_ROOT = $AndroidSdkPath
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:Path"

Write-Host "Using JAVA_HOME=$env:JAVA_HOME"
Write-Host "Using ANDROID_HOME=$env:ANDROID_HOME"

& ".\gradlew.bat" ":app:assembleDebug" ":app:assembleDebugAndroidTest" ":app:testDebugUnitTest" ":app:lintDebug" "--stacktrace"

$DebugApk = Join-Path $ProjectRoot "app\build\outputs\apk\debug\app-debug.apk"
$AndroidTestApk = Join-Path $ProjectRoot "app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk"

foreach ($Artifact in @($DebugApk, $AndroidTestApk)) {
    if (-not (Test-Path $Artifact)) {
        throw "Expected APK artifact was not generated: $Artifact"
    }

    $Item = Get-Item $Artifact
    Write-Host ("Verified APK: {0} ({1} bytes)" -f $Item.FullName, $Item.Length)
}

$AdbDevicesOutput = & "adb" "devices"
Write-Host ($AdbDevicesOutput -join [Environment]::NewLine)

$ConnectedDevices = @(
    $AdbDevicesOutput |
        Select-String -Pattern "^\S+\s+device$" |
        ForEach-Object { $_.Matches.Value }
)

if ($RunConnectedTests) {
    if ($ConnectedDevices.Count -eq 0) {
        throw "RunConnectedTests was requested, but adb did not report an attached device."
    }

    & ".\gradlew.bat" ":app:connectedDebugAndroidTest" "--stacktrace"
} elseif ($ConnectedDevices.Count -eq 0) {
    Write-Host "No adb device detected. Skipping connectedDebugAndroidTest and manual notification verification."
} else {
    Write-Host "adb device detected. Re-run with -RunConnectedTests to execute connectedDebugAndroidTest."
}
