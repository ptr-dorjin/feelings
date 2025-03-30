#!/bin/bash

source common.sh

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device false

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

    kill_device $device
done
