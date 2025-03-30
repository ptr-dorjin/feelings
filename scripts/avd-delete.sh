#!/bin/bash

source common.sh

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device

    echo "Deleting $device"

    $AVD_MANAGER delete avd -n $device
done
