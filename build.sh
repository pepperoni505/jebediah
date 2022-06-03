docker run \
    --rm \
    -v `pwd`:/jebediah \
    -v "$HOME/.dockercache/gradle":"/root/.gradle" \
    mingc/android-build-box bash \
    -c 'update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java;cd /jebediah;./gradlew assembleDebug'