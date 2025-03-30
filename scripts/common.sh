#!/bin/bash

SDK_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/sdkmanager
AVD_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/avdmanager
EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb

declare -a filtered_devices

filter_devices() {
    resume_from=$1
    mapfile -t all_devices < <($EMULATOR -list-avds)

    echo "Resume from device: $resume_from"
    already_resumed=0
    for device in "${all_devices[@]}"; do
        if [[ $resume_from && "$device" != "$resume_from" && $already_resumed == 0 ]] ; then
            echo "Skipping $device"
            continue
        fi
        already_resumed=1
        filtered_devices+=($device)
    done
}

start_device() {
    device=$1
    cold=$2
    echo "Starting $device cold=$cold"
    if $cold; then
        $EMULATOR -avd ${device} -no-snapshot-load 2>&1 1>/dev/null &
    else
        $EMULATOR -avd ${device} 2>&1 1>/dev/null &
    fi
    EMULATOR_PID=$!
    echo "Emulator PID: $EMULATOR_PID"

    wait_for_device
    echo "Started $device"
}

wait_for_device() {
    echo "Waiting for device"
    $ADB wait-for-device

    while [ "$(adb shell getprop sys.boot_completed)" != "1" ]; do
        echo "Waiting for device to finish booting"
        sleep 2
    done
    echo "Device is ready"
}

kill_device() {
    device=$1
    echo "Killing  $device"
    $ADB </dev/null emu kill
    echo "Killed $device"
    sleep 5
}

should_skip() {
    device=$1
    resume_from=$2
    already_resumed=$3
    if [[ $resume_from && "$device" != "$resume_from" && $already_resumed == 0 ]]; then
        return
    fi
    false
}