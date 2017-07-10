package feelings.helper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import feelings.helper.alarm.AlarmService;
import feelings.helper.settings.Settings;
import feelings.helper.settings.SettingsStore;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            for (Settings settings : SettingsStore.getAllSettings(context)) {
                if (settings.isOn()) {
                    AlarmService.setAlarm(context, settings);
                }
            }
        }
    }
}
