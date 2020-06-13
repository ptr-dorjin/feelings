#!/bin/bash

EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb
LOGS=~/dev/feelings/app/build/outputs/logs

TIMESTAMP=$(date +"%Y%m%d%H%M")
mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Starting $device"
  ${EMULATOR} </dev/null -avd ${device} &
  #EMULATOR_PID=$!

  $ADB </dev/null wait-for-device shell getprop init.svc.bootanim
  echo "Started $device"

  echo "Uninstalling"

  if ($ADB shell pm list packages | grep feelings.guide); then
    echo "Logging to: ${LOGS}/uninstall-main-${device}-${TIMESTAMP}.log"
    $ADB </dev/null uninstall feelings.guide \
      2>&1 | tee ${LOGS}/uninstall-main-${device}-${TIMESTAMP}.log
  else
    echo "Could not find feelings.guide"
  fi

  if ($ADB shell pm list packages | grep feelings.guide.test); then
    echo "Logging to: ${LOGS}/uninstall-test-${device}-${TIMESTAMP}.log"
    $ADB </dev/null uninstall feelings.guide.test \
      2>&1 | tee ${LOGS}/uninstall-test-${device}-${TIMESTAMP}.log
  else
    echo "Could not find feelings.guide.test"
  fi

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done
