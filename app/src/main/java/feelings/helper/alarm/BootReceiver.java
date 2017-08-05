package feelings.helper.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            for (Schedule schedule : ScheduleService.getAllSchedules(context)) {
                if (schedule.isOn()) {
                    AlarmService.setAlarm(context, schedule);
                }
            }
        }
    }
}
