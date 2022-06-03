# jebediah

NASA Kibo-RPC Submission

#### Install Android SDK

- Go to <https://developer.android.com/studio/archive> and agree to the terms and conditions
- Scroll down to `Android Studio 3.4.1` and download the Windows 64-bit installer
- Run the installer and don't change any options
- Launch Android Studio and download required components
- On the Android Studio splash screen, go to `Configure` -> `SDK Manager`
- Uncheck `Android API 32`
- Enable `Show Package Details`
- Scroll down to `Android 8.0 (Oreo)` and enable `Android SDK Platform 26`
- Scroll down to `Android 7.1.1 (Nougat)` and enable `Android SDK Platform 25`
- Switch to the `SDK Tools` tab and enable `Show Package Details`
- Disable `32.0.0` and `32.1.0-rc1`
- Enable `25.0.3` and `26.0.2`
- Click `Apply` and install the new components

**Important: If prompted, do NOT update Gradle**