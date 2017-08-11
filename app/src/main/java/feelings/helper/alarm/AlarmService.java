package feelings.helper.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import feelings.helper.schedule.Schedule;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.helper.util.DateTimeUtil.LOG_FORMATTER;

public class AlarmService {
    private static final String TAG = AlarmService.class.getSimpleName();

    public static void setAlarm(Context context, Schedule schedule) {
        int questionId = schedule.getQuestionId();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(QUESTION_ID_PARAM, questionId);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_ONE_SHOT);

        LocalDateTime nextTime = schedule.getRepeat().getNextTime();
        long timeMillis = nextTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        }
        Log.i(TAG, String.format("setAlarm: qId = %s, next time: %s, from millis: %s",
                questionId,
                nextTime.format(LOG_FORMATTER),
                Instant.ofEpochMilli(timeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(LOG_FORMATTER)));
    }

    public static void cancelAlarm(Context context, int questionId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pintent);
        Log.i(TAG, "cancelAlarm: qId = " + questionId);
    }
}
