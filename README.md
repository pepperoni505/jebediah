# jebediah

NASA Kibo-RPC Submission

## Required software

Windows 10 or newer

[git](https://git-scm.com/downloads)

[Docker](https://docs.docker.com/get-docker/) (You do not need an account to use Docker)

[wsl](https://docs.microsoft.com/en-us/windows/wsl/install)

## Cloning and setup

**Run all of these commands inside of a WSL terminal**

```shell
git clone https://github.com/pepperoni505/jebediah
cd jebediah/sim
./build.sh
```

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

- Download and install [VcXsrv](https://sourceforge.net/projects/vcxsrv/files/latest/download)
- Run `sudo nano ~/.bashrc` and add the following lines
   - ```shell
     export DISPLAY=$(ip route list default | awk '{print $3}'):0
     export LIBGL_ALWAYS_INDIRECT=1
     ```

- Run `sudo apt update` followed by `sudo apt install x11-apps` and restart WSL
- Launch XLaunch and select `Multiple Windows`
- Click `Next` and enable `Start no client`
- Click `Next` and disable `Native opengl` and enable `Disable access control`
- Click `Next` and click `Save configuration`. Put this file somewhere you will remember
- Click `Finish`
- Press `Windows Key` + `R` and type `shell:startup`
- Copy the config file into this folder

#### WSL configuration

- Run `sudo nano /etc/hosts` and add the following lines
  - ```text
    127.0.0.1 hlp
    127.0.0.1 mlp
    127.0.0.1 llp
    ```

## How to run the local simulator

`cd sim`
`bash ./run.sh`

##### TODO: further instructions
