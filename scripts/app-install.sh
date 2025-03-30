#!/bin/bash

source common.sh

# prerequisite: run `./gradlew clean assembleDebug assembleAndroidTest`
# which will build two apks respectively:
# ~/dev/feelings/app/build/outputs/apk/debug/app-debug.apk
# ~/dev/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

APK_MAIN=~/feelings/app/build/outputs/apk/debug/app-debug.apk
APK_TEST=~/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device false

    echo "Installing"

  $ADB </dev/null install -r -t $APK_MAIN
  $ADB </dev/null install -r -t $APK_TEST

    kill_device $device
done
