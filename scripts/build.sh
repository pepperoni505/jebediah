# cd to root folder, no matter where we are calling this script
cd "$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )/.."

docker run \
    --rm \
    -v `pwd`:/jebediah \
    -v "$HOME/.dockercache/gradle":"/root/.gradle" \
    mingc/android-build-box bash \
    -c 'update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java;cd /jebediah;./gradlew assembleDebug'