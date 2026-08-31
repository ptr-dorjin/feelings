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
    "android-36"
)

# 1. Download images
# Uses the plain "default" (AOSP, no bundled Google apps) image, not "google_apis": the app has
# no Play Services dependency, and the bundled GMS/YouTube/Photos/Dialer/Calendar apps in the
# google_apis image otherwise churn in the background and are a common source of system-wide ANRs.
for api in "${api_versions[@]}"
do
    echo "Downloading image 'system-images;$api;default;x86_64'"
    $SDK_MANAGER "system-images;$api;default;x86_64"
done

# 2. Accept licences
$SDK_MANAGER --licenses

# 3. Create devices
for api in "${api_versions[@]}"
do
    echo "Creating virtual device $api"
    $AVD_MANAGER --silent create avd --force --name $api --abi default/x86_64 \
      --package "system-images;$api;default;x86_64" --device "pixel"
    echo "Done"
done

# 4. Let initial bootstrap finish for all devices
filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device
    start_device $device true
    kill_device $device
done
