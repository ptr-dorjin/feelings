#!/bin/bash

AVD_MANAGER=~/Android/Sdk/cmdline-tools/latest/bin/avdmanager
EMULATOR=~/Android/Sdk/emulator/emulator

mapfile -t DEVICES < <($EMULATOR -list-avds)

for device in "${DEVICES[@]}"; do
  printf "\n====================== %s ======================\n" $device
  echo "Deleting $device"

  $AVD_MANAGER delete avd -n $device
done
