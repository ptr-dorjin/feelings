#!/bin/bash

SDK_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/sdkmanager
AVD_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/avdmanager
EMULATOR=~/Android/Sdk/emulator/emulator
ADB=~/Android/Sdk/platform-tools/adb

# prerequisite: run `./gradlew clean assembleDebug assembleAndroidTest`
APK_MAIN=~/feelings/app/build/outputs/apk/debug/app-debug.apk
APK_TEST=~/feelings/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

declare -a filtered_devices

# Serial of the emulator most recently started by start_device, e.g. "emulator-5554".
# Every adb call must target it explicitly with -s: a bare $ADB call is ambiguous
# whenever more than one emulator is online at once.
DEVICE_SERIAL=

list_emulator_serials() {
    $ADB devices | awk '$2 == "device" && $1 ~ /^emulator-/ { print $1 }'
}

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

    mapfile -t before_serials < <(list_emulator_serials)

    if $cold; then
        $EMULATOR -avd ${device} -no-snapshot-load 2>&1 1>/dev/null &
    else
        $EMULATOR -avd ${device} 2>&1 1>/dev/null &
    fi
    EMULATOR_PID=$!
    echo "Emulator PID: $EMULATOR_PID"

    echo "Waiting for a new emulator serial to appear"
    DEVICE_SERIAL=
    while [[ -z "$DEVICE_SERIAL" ]]; do
        mapfile -t current_serials < <(list_emulator_serials)
        for serial in "${current_serials[@]}"; do
            if [[ ! " ${before_serials[*]} " == *" $serial "* ]]; then
                DEVICE_SERIAL=$serial
                break
            fi
        done
        [[ -z "$DEVICE_SERIAL" ]] && sleep 1
    done
    echo "Assigned serial: $DEVICE_SERIAL"

    wait_for_device
    echo "Started $device ($DEVICE_SERIAL)"
}

wait_for_device() {
    echo "Waiting for device $DEVICE_SERIAL"
    $ADB -s $DEVICE_SERIAL wait-for-device

    while [ "$($ADB -s $DEVICE_SERIAL shell getprop sys.boot_completed)" != "1" ]; do
        echo "Waiting for device to finish booting"
        sleep 2
    done

    # On a quick-boot resume, boot_completed is already 1 in the snapshot even though
    # system services haven't reattached yet; only a real pm round-trip proves it's up.
    while ! $ADB -s $DEVICE_SERIAL shell pm list packages >/dev/null 2>&1; do
        echo "Waiting for package service to be ready"
        sleep 2
    done
    echo "Device is ready"
}

# retry <attempts> <delay-seconds> <command...>
# Retries a flaky adb call, e.g. right after a device is "ready" but services are
# still reattaching (broken pipe on the first request or two).
retry() {
    attempts=$1
    delay=$2
    shift 2
    n=1
    until "$@"; do
        if [[ $n -ge $attempts ]]; then
            echo "Command failed after $attempts attempts: $*"
            return 1
        fi
        echo "Command failed (attempt $n/$attempts), retrying in ${delay}s: $*"
        n=$((n + 1))
        sleep "$delay"
    done
}

# Installs the app + test APKs on the currently started device ($DEVICE_SERIAL).
# Not relied on to persist across script runs via the emulator's own snapshot save
# (see kill_device) - callers install fresh into their own live session instead.
install_apks() {
    echo "Installing"
    retry 5 5 $ADB -s $DEVICE_SERIAL install -r -t $APK_MAIN </dev/null || return 1
    retry 5 5 $ADB -s $DEVICE_SERIAL install -r -t $APK_TEST </dev/null || return 1
}

kill_device() {
    device=$1
    echo "Killing  $device ($DEVICE_SERIAL, pid $EMULATOR_PID)"
    $ADB -s $DEVICE_SERIAL </dev/null emu kill

    # `emu kill` requests shutdown but the process still has to flush its snapshot to disk;
    # it drops off `adb devices` well before that finishes. Wait for the process itself, or
    # starting the next emulator too soon can starve/OOM-kill this one mid-write and corrupt
    # its snapshot.
    echo "Waiting for emulator process $EMULATOR_PID to fully exit"
    timeout=120
    while kill -0 $EMULATOR_PID 2>/dev/null; do
        sleep 1
        timeout=$((timeout - 1))
        if [[ $timeout -le 0 ]]; then
            echo "Timed out waiting for emulator process $EMULATOR_PID to exit, moving on"
            break
        fi
    done
    echo "Killed $device"
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