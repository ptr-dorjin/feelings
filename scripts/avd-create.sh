#!/bin/bash

source common.sh

declare -a api_versions=(
    "android-26"
    "android-28"
    "android-29"
    "android-30"
    "android-31"
    "android-33"
    "android-34"
    "android-35"
)

# 1. Download images
for api in "${api_versions[@]}"
do
    echo "Downloading image 'system-images;$api;google_apis;x86_64'"
    $SDK_MANAGER "system-images;$api;google_apis;x86_64"
done

# 2. Accept licences
$SDK_MANAGER --licenses

# 3. Create devices
for api in "${api_versions[@]}"
do
    echo "Creating virtual device $api"
    $AVD_MANAGER --silent create avd --force --name $api --abi google_apis/x86_64 \
      --package "system-images;$api;google_apis;x86_64" --device "pixel"
    echo "Done"
done

# 4. Let initial bootstrap finish for all devices
filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device true
    kill_device $device
done
