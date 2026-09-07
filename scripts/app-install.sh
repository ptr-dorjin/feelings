#!/bin/bash

source common.sh

# Installs the app on all virtual devices and leaves it there (unlike
# run-tests-on-all-devices.sh, which installs fresh into each test run and doesn't
# depend on this having been run first). Useful for manual poking at a device.

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device false

    install_apks

    kill_device $device
done
