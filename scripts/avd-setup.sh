#!/bin/bash

EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb

# 1. Switch animation off
mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device

  if (! grep hw.keyboard < ~/.android/avd/$device.avd/config.ini); then
    echo "settings hw.keyboard=yes"
    echo "hw.keyboard=yes" >> ~/.android/avd/$device.avd/config.ini
  fi

  echo "Starting $device"
  ${EMULATOR} </dev/null -avd ${device} &
  #EMULATOR_PID=$!

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Switching animation off"

  # known issue: `settings` is not available in API 16
  $ADB </dev/null shell settings put global window_animation_scale 0
  $ADB </dev/null shell settings put global transition_animation_scale 0
  $ADB </dev/null shell settings put global animator_duration_scale 0
  sleep 5

  $ADB reboot

  sleep 10

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done

# 2. Switch keyboard auto-correction off
# manually switch off virtual keyboard autocomplete on each device
# couldn't find a way to do this using adb
