#!/bin/bash

source common.sh

LOGS=~/feelings/app/build/outputs/logs
TIMESTAMP=$(date +"%Y%m%d%H%M")

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device false

    # Install fresh into this session rather than relying on a previous app-install.sh run
    # having persisted the app via the emulator's own snapshot save - see kill_device for why
    # that isn't reliable across separate script invocations.
    if install_apks; then
        echo "Running tests"

        echo "Logging to: ${LOGS}/test-run-${device}-${TIMESTAMP}.log"
        $ADB -s $DEVICE_SERIAL </dev/null shell am instrument -w -e package feelings.guide \
            feelings.guide.test/feelings.guide.HiltTestRunner \
            2>&1 | tee ${LOGS}/test-run-${device}-${TIMESTAMP}.log
    else
        echo "Skipping tests on $device: install failed"
    fi

    kill_device $device
done
