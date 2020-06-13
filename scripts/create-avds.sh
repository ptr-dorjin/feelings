#!/bin/bash

SDK_MANAGER=~/Android/Sdk/tools/bin/sdkmanager
ADB_MANAGER=~/Android/Sdk/tools/bin/avdmanager

$SDK_MANAGER 'system-images;android-15;google_apis;x86'
$SDK_MANAGER 'system-images;android-21;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-23;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-24;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-26;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-28;google_apis;x86_64'
$SDK_MANAGER 'system-images;android-29;google_apis;x86_64'

$SDK_MANAGER --licenses

$ADB_MANAGER --silent create avd --force --name api15-android4 --abi google_apis/x86 \
    --package 'system-images;android-15;google_apis;x86' --device "Nexus 4"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api21-android5 --abi google_apis/x86_64 \
    --package 'system-images;android-21;google_apis;x86_64' --device "Nexus 6"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api23-android6 --abi google_apis/x86_64 \
    --package 'system-images;android-23;google_apis;x86_64' --device "Nexus 6P"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api24-android7 --abi google_apis/x86_64 \
    --package 'system-images;android-24;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api26-android8 --abi google_apis/x86_64 \
    --package 'system-images;android-26;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api28-android9 --abi google_apis/x86_64 \
    --package 'system-images;android-28;google_apis;x86_64' --device "pixel"
printf "\ndone\n"
$ADB_MANAGER --silent create avd --force --name api29-android10 --abi google_apis/x86_64 \
    --package 'system-images;android-29;google_apis;x86_64' --device "pixel"
printf "\ndone\n"

# after that manually switch off virtual keyboard autocomplete on each device