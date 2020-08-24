#!/bin/bash

# prerequisite: run `./gradlew clean assembleDebug assembleAndroidTest`
# which will build two apks respectively:
# ~/dev/feelings/app/build/outputs/apk/debug/app-debug.apk
# ~/dev/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb
APK_MAIN=~/dev/feelings/app/build/outputs/apk/debug/app-debug.apk
APK_TEST=~/dev/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Starting $device"
  ${EMULATOR} </dev/null -avd ${device} &

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Installing"

  $ADB </dev/null install -r -t $APK_MAIN
  $ADB </dev/null install -r -t $APK_TEST

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done
