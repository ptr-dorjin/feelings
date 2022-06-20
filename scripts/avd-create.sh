#!/bin/bash

SDK_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/sdkmanager
AVD_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/avdmanager
EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb

# 1. Download images
$SDK_MANAGER 'system-images;android-21;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-23;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-24;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-26;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-28;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-29;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-30;google_apis;x86_64'

# 2. Accept licences
$SDK_MANAGER --licenses

# 3. Create devices
$AVD_MANAGER --silent create avd --force --name api21-android5 --abi google_apis/x86_64 \
  --package 'system-images;android-21;google_apis;x86_64' --device "Nexus 6"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api23-android6 --abi google_apis/x86_64 \
  --package 'system-images;android-23;google_apis;x86_64' --device "Nexus 6P"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api24-android7 --abi google_apis/x86_64 \
  --package 'system-images;android-24;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api26-android8 --abi google_apis/x86_64 \
  --package 'system-images;android-26;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api28-android9 --abi google_apis/x86_64 \
  --package 'system-images;android-28;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api29-android10 --abi google_apis/x86_64 \
  --package 'system-images;android-29;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$AVD_MANAGER --silent create avd --force --name api30-android11 --abi google_apis/x86_64 \
  --package 'system-images;android-30;google_apis;x86_64' --device "pixel"
printf "\ndone\n"

# 4. Let initial bootstrap finish for all devices
SLEEP_TIME=120
mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Starting $device"

  ${EMULATOR} </dev/null -avd ${device} &

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Sleeping ${SLEEP_TIME} seconds to let initial bootstrap finish ..."
  sleep $SLEEP_TIME

  $ADB </dev/null emu kill
  echo "Killed $device"
done
