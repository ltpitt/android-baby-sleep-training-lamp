[![build](https://github.com/ltpitt/java-spring-cloud-drive/workflows/build/badge.svg)](https://github.com/ltpitt/java-spring-cloud-drive/actions?query=workflow%3AAndroid%20CI)
[![GitHub Issues](https://img.shields.io/github/issues-raw/ltpitt/android-baby-sleep-training-lamp)](https://github.com/ltpitt/android-baby-sleep-training-lamp/issues)
[![Total Commits](https://img.shields.io/github/last-commit/ltpitt/android-baby-sleep-training-lamp)](https://github.com/ltpitt/android-baby-sleep-training-lamp/commits)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/4w/ltpitt/android-baby-sleep-training-lamp?foo=bar)](https://github.com/ltpitt/android-baby-sleep-training-lamp/commits)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/ltpitt/android-baby-sleep-training-lamp/blob/master/LICENSE)
![Contributions welcome](https://img.shields.io/badge/contributions-welcome-orange.svg)

Little Cloud Android App - Baby Sleep Training Lamp
===================================

An Android app to control the DIY [Little Cloud - Baby Sleep Training Lamp](https://github.com/ltpitt/c-photon-baby-sleep-training-lamp).

This app allows you to:
*   Control the RGB LED color and brightness.
*   Play music/audio tracks via the DFPlayer Mini module.
*   Manage Particle Cloud credentials securely.

App images
--------------
<img src="/screenshots/little_cloud_android_app_light.png" width="25%">
<img src="/screenshots/little_cloud_android_app_audio.png" width="25%">


Pre-requisites
--------------

- Android SDK v35 (Compile SDK)
- Min SDK: 24 (Android 7.0)
- Target SDK: 35 (Android 15)
- Android Build Tools


Getting Started
---------------

This project uses the Gradle build system. To build this project, use the
`./gradlew build` command or use "Import Project" in Android Studio.

### Configuration

To control your lamp, you need to configure the app with your Particle Cloud credentials:

1.  Go to the **Settings** tab in the app.
2.  Enter your **Particle API URL** (default: `https://api.particle.io/v1/devices`).
3.  Enter your **Device ID** (from your Particle console).
4.  Enter your **Access Token** (from your Particle console).
5.  Click **Test Connection** to verify connectivity.
6.  Click the **Save** (Floppy Disk) FAB button to securely store your credentials.

### Security

This app uses `EncryptedSharedPreferences` to securely store your API Access Token and other sensitive settings. Network communication is enforced to use HTTPS.

Contribution guidelines
---------------
* If you have any idea or suggestion contact directly the Repo Owner
