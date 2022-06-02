# jebediah

NASA Kibo-RPC Submission

## Required software

Windows 10 or newer

[git](https://git-scm.com/downloads)

[Docker](https://docs.docker.com/get-docker/) (You do not need an account to use Docker)

[wsl](https://docs.microsoft.com/en-us/windows/wsl/install)

#### Docker configuration

- Go to Docker Desktop and open your settings.
- Go to `Resources` -> `WSL Integration`. Under `Enable integration with additional distros:`, enable your WSL distro and click `Apply & Restart`

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

#### Install and setup XServer

- Download and install [VcXsrv](https://sourceforge.net/projects/vcxsrv/files/latest/download) in your `Program Files` folder.

TODO: auto start and detect if already running, as well as describe sim build process

## How to run the local simulator

1. Make sure XServer is running
    - Run the command `"C:\Program Files\VcXsrv\xlaunch.exe\" -run ./scripts/config.xlaunch`
    - **Do NOT run more than one instance of XServer!**
2. Run `./scripts/run.sh` in a WSL terminal instance

##### TODO: further instructions
