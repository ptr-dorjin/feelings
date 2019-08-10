#!/bin/bash

declare EMULATOR=~/dev/ide/android-studio/emulator/emulator
declare LOGS=~/AndroidProjects/feelings/app/build/outputs/logs

declare timestamp=$(date +"%Y%m%d%H%M")
declare DEVICES=($(${EMULATOR} -list-avds))

for device in "${DEVICES[@]}"
do
    echo "starting $device"
    ${EMULATOR} </dev/null -avd ${device} &
    EMULATOR_PID=$!

    adb </dev/null wait-for-device shell getprop init.svc.bootanim
    echo "started $device"

    #adb shell pm list instrumentation
    adb </dev/null shell am instrument -w \
        -e package feelings.guide \
        feelings.guide.test/androidx.test.runner.AndroidJUnitRunner \
        2>&1 | tee ${LOGS}/test-run-${device}-${timestamp}.log
    adb </dev/null emu kill
    sleep 5
done
