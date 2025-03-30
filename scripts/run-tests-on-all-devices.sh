#!/bin/bash

source common.sh

LOGS=~/feelings/app/build/outputs/logs
TIMESTAMP=$(date +"%Y%m%d%H%M")

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device false

    echo "Running tests"

    echo "Logging to: ${LOGS}/test-run-${device}-${TIMESTAMP}.log"
    if ($ADB shell pm list instrumentation | grep feelings.guide.test); then
        $ADB </dev/null shell am instrument -w -e package feelings.guide \
            feelings.guide.test/androidx.test.runner.AndroidJUnitRunner \
            2>&1 | tee ${LOGS}/test-run-${device}-${TIMESTAMP}.log
    else
        echo "Could not find feelings.guide.test"
    fi

    kill_device $device
done
