package feelings.helper.schedule;

import android.content.Context;

import java.util.Collection;

import feelings.helper.alarm.AlarmService;

public class ScheduleService {

    /**
     * Create or update
     */
    public static boolean saveSchedule(Context context, Schedule schedule) {
        boolean success = ScheduleStore.saveSchedule(context, schedule);
        if (success) {
            if (schedule.isOn()) {
                AlarmService.setAlarm(context, schedule);
            } else {
                AlarmService.cancelAlarm(context, schedule.getQuestionId());
            }
        }
        return success;
    }

    /**
     * Update only on/off flag
     */
    public static boolean switchOnOff(Context context, Schedule schedule) {
        boolean success = ScheduleStore.switchOnOff(context, schedule.getQuestionId(), schedule.isOn());
        if (success) {
            if (schedule.isOn()) {
                AlarmService.setAlarm(context, schedule);
            } else {
                AlarmService.cancelAlarm(context, schedule.getQuestionId());
            }
        }
        return success;
    }

    public static Schedule getSchedule(Context context, int questionId) {
        return ScheduleStore.getSchedule(context, questionId);
    }

    public static Collection<Schedule> getAllSchedules(Context context) {
        return ScheduleStore.getAllSchedules(context);
    }
}
