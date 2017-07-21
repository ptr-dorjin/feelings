package feelings.helper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import feelings.helper.alarm.AlarmService;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleStore;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            for (Schedule schedule : ScheduleStore.getAllSchedules(context)) {
                if (schedule.isOn()) {
                    AlarmService.setAlarm(context, schedule);
                }
            }
        }
    }
}
