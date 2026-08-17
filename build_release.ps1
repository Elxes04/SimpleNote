# SimpleNote Release Automation Script

$ErrorActionPreference = "Stop"

Write-Host "Starting SimpleNote Release Build..." -ForegroundColor Cyan

# 0. Handle JAVA_HOME if not set
if (-not $env:JAVA_HOME -and -not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "JAVA_HOME not found. Attempting to locate Android Studio JDK..." -ForegroundColor Yellow
    $asJdk = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path $asJdk) {
        $env:JAVA_HOME = $asJdk
        Write-Host "Using Android Studio JDK: $asJdk" -ForegroundColor Gray
    } else {
        Write-Error "Could not find a valid JDK. Please set JAVA_HOME in your system environment variables."
    }
}

# 1. Clean and Build
Write-Host "Cleaning and building APKs..." -ForegroundColor Gray
if ($IsWindows) {
    .\gradlew.bat clean assembleRelease
} else {
    ./gradlew clean assembleRelease
}

# 2. Parse version from build.gradle.kts
$gradleFile = Get-Content "app/build.gradle.kts" -Raw
$versionName = ($gradleFile | Select-String -Pattern 'versionName = "(.*)"').Matches.Groups[1].Value
$versionCode = ($gradleFile | Select-String -Pattern 'versionCode = (\d+)').Matches.Groups[1].Value

if (-not $versionName) {
    Write-Error "Could not find versionName in build.gradle.kts"
}

Write-Host "Found Version: $versionName (Code: $versionCode)" -ForegroundColor Green

# 3. Prepare output directory
$releaseDir = "build_outputs/v$versionName"
if (!(Test-Path $releaseDir)) {
    New-Item -ItemType Directory -Path $releaseDir | Out-Null
}

# 4. Move and rename APKs
Write-Host "Organizing APKs..." -ForegroundColor Gray

$apkSource = "app/build/outputs/apk/release"
$apks = Get-ChildItem -Path $apkSource -Filter "*.apk" -Recurse

foreach ($apk in $apks) {
    $abi = ""
    if ($apk.Name -like "*universal*") {
        $abi = "universal"
    } elseif ($apk.Name -like "*arm64-v8a*") {
        $abi = "arm64-v8a"
    } elseif ($apk.Name -like "*armeabi-v7a*") {
        $abi = "armeabi-v7a"
    } elseif ($apk.Name -like "*x86_64*") {
        $abi = "x86_64"
    } elseif ($apk.Name -like "*x86*") {
        $abi = "x86"
    }

    if ($abi) {
        $newName = "SimpleNote-v$versionName-$abi.apk"
        Copy-Item $apk.FullName -Destination "$releaseDir/$newName"
        Write-Host " Created: $newName" -ForegroundColor White
    }
}

Write-Host "`nRelease build complete! Files are in: $releaseDir" -ForegroundColor Cyan
Write-Host "Note: These APKs use the debug key for now as configured in build.gradle.kts" -ForegroundColor Yellow
