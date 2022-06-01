docker run \
    --rm \
    -v `pwd`:/jebediah \
    mingc/android-build-box bash \
    -c 'cd jebediah;./gradlew build'