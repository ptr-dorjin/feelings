#!/bin/bash

# prerequisite: run `./gradlew assembleDebug assembleAndroidTest`
# which will build two apks respectively:
# ~/dev/feelings/app/build/outputs/apk/debug/app-debug.apk
# ~/dev/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb
LOGS=~/dev/feelings/app/build/outputs/logs
APK_MAIN=~/dev/feelings/app/build/outputs/apk/debug/app-debug.apk
APK_TEST=~/dev/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

TIMESTAMP=$(date +"%Y%m%d%H%M")
mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Starting $device"
  ${EMULATOR} </dev/null -avd ${device} &
  #EMULATOR_PID=$!

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Installing"

  echo "Logging to: ${LOGS}/install-main-${device}-${TIMESTAMP}.log"
  $ADB </dev/null install -r -t $APK_MAIN \
    2>&1 | tee ${LOGS}/install-main-${device}-${TIMESTAMP}.log

  echo "Logging to: ${LOGS}/install-test-${device}-${TIMESTAMP}.log"
  $ADB </dev/null install -r -t $APK_TEST \
    2>&1 | tee ${LOGS}/install-test-${device}-${TIMESTAMP}.log

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done
