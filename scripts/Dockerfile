FROM mingc/android-build-box

RUN update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

RUN apt-get update && apt-get install -y \
    sudo \
    iproute2 \
    gnome-terminal \
    build-essential

ENV ANDROID_PATH=/project/astrobee_android

# Clone needed repos
RUN git clone https://github.com/nasa/astrobee_android.git
RUN cd ./astrobee_android && git checkout a8560ab0270ac281d8eadeb48645f4224582985e

RUN cd ${ANDROID_PATH}/core_apks/guest_science_manager && ANDROID_HOME=$ANDROID_HOME ./gradlew assembleDebug

ENV ADB_INSTALL_TIMEOUT=30

RUN sdkmanager "system-images;android-25;default;x86"

RUN echo "no" | avdmanager create avd \
    -n AstrobeeAndroidSim \
    -k "system-images;android-25;default;x86" \
    --force

ENV ANDROID_PATH=/project/astrobee_android
ENV EMULATOR=${ANDROID_HOME}/emulator
ENV AVD="AstrobeeAndroidSim"