#!/bin/bash

source common.sh

filter_devices $1
for device in "${filtered_devices[@]}"; do
    printf "\n====================== %s ======================\n" $device

    # Enable hardware keyboard
    echo "Setting hw.keyboard=yes"
    config_file=~/.android/avd/$device.avd/config.ini
    config_file_backup=~/.android/avd/$device.avd/config.ini.bkp
    cp $config_file $config_file_backup
    echo "Created a backup of the config file: $config_file_backup"
    sed 's/hw.keyboard *=.*/hw.keyboard=yes/' -i $config_file
    if (! grep -P "hw.keyboard *= *yes" < $config_file); then
        echo "hw.keyboard=yes" >> $config_file
    fi

    # Enable GPU acceleration - avdmanager's default (hw.gpu.enabled=no) forces software
    # rendering, which is slow enough to trigger system-wide ANRs (launcher, SystemUI, etc).
    echo "Setting hw.gpu.enabled=yes, hw.gpu.mode=host"
    sed 's/hw.gpu.enabled *=.*/hw.gpu.enabled=yes/' -i $config_file
    if (! grep -P "hw.gpu.enabled *= *yes" < $config_file); then
        echo "hw.gpu.enabled=yes" >> $config_file
    fi
    sed 's/hw.gpu.mode *=.*/hw.gpu.mode=host/' -i $config_file
    if (! grep -P "hw.gpu.mode *= *host" < $config_file); then
        echo "hw.gpu.mode=host" >> $config_file
    fi

    # Bump RAM - the 2G default is too tight once background processes pile up, causing
    # memory pressure that also shows up as system-wide ANRs.
    echo "Setting hw.ramSize=4G"
    sed 's/hw.ramSize *=.*/hw.ramSize=4G/' -i $config_file
    if (! grep -P "hw.ramSize *= *4G" < $config_file); then
        echo "hw.ramSize=4G" >> $config_file
    fi

    start_device $device true

    # Switch animation off
    echo "Switching animation off"

    $ADB </dev/null shell settings put global window_animation_scale 0
    $ADB </dev/null shell settings put global transition_animation_scale 0
    $ADB </dev/null shell settings put global animator_duration_scale 0
    sleep 5

    echo "Rebooting to apply the new settings"
    $ADB reboot

    wait_for_device

    kill_device $device
done

# 2. Switch keyboard auto-correction off
# manually switch off virtual keyboard autocomplete on each device
# couldn't find a way to do this using adb
