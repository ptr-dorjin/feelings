package feelings.helper.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.helper.util.DateTimeUtil.LOG_FORMATTER;

public class AlarmService extends IntentService {
    private static final String SCHEDULE = "schedule";

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

    public AlarmService() {
        super(AlarmService.class.getSimpleName());
    }

    /**
     * Static convenient method. Starts self as a service
     */
    public static void setAlarm(Context context, Schedule schedule) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(SCHEDULE, schedule);
        context.startService(intent);
    }

    public static void restartAll(Context context) {
        for (Schedule schedule : ScheduleService.getAllSchedules(context)) {
            if (schedule.isOn()) {
                setAlarm(context, schedule);
            }
        }
    }

    public static void cancelAlarm(Context context, int questionId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pintent);
        log.info("question {}", questionId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setOneAlarm((Schedule) intent.getParcelableExtra(SCHEDULE));
        } else {
            //set all alarms
            for (Schedule schedule : ScheduleService.getAllSchedules(this)) {
                if (schedule.isOn()) {
                    setOneAlarm(schedule);
                }
            }
        }
    }

    private void setOneAlarm(Schedule schedule) {
        int questionId = schedule.getQuestionId();

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra(QUESTION_ID_PARAM, questionId);
        PendingIntent pintent = PendingIntent.getBroadcast(this, questionId, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        LocalDateTime nextTime = schedule.getRepeat().getNextTime();
        long timeMillis = nextTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        }
        log.info("question {}, next time: {}", questionId, nextTime.format(LOG_FORMATTER));
    }
}
