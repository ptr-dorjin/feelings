#!/bin/bash

EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb

mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Starting $device"
  ${EMULATOR} </dev/null -avd ${device} &

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Uninstalling"

  if ($ADB shell pm list packages | grep feelings.guide); then
    $ADB </dev/null uninstall feelings.guide
  else
    echo "Could not find feelings.guide"
  fi

  if ($ADB shell pm list packages | grep feelings.guide.test); then
    $ADB </dev/null uninstall feelings.guide.test
  else
    echo "Could not find feelings.guide.test"
  fi

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done
