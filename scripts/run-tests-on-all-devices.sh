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

  echo "Running tests"

  echo "Logging to: ${LOGS}/test-run-${device}-${TIMESTAMP}.log"
  if ($ADB shell pm list instrumentation | grep feelings.guide.test); then
    $ADB </dev/null shell am instrument -w -e package feelings.guide \
      feelings.guide.test/androidx.test.runner.AndroidJUnitRunner \
      2>&1 | tee ${LOGS}/test-run-${device}-${TIMESTAMP}.log
  else
    echo "Could not find feelings.guide.test"
  fi

  $ADB </dev/null emu kill
  echo "Killed $device"

  sleep 5
done
