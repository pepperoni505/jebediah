cd $ANDROID_PATH/scripts
./launch_emulator.sh -n

adb wait-for-device

cd $ANDROID_PATH/core_apks/guest_science_manager
adb install -g -r activity/build/outputs/apk/activity-debug.apk
cd /jebediah
adb install -g -r app/build/outputs/apk/app-debug.apk

$ANDROID_PATH/scripts/gs_manager.sh start