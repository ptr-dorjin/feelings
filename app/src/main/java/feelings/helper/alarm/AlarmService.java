package feelings.helper.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.threeten.bp.ZoneId;

import feelings.helper.schedule.Schedule;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

public class AlarmService {

    public static void setAlarm(Context context, Schedule schedule) {
        int questionId = schedule.getQuestionId();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(QUESTION_ID_PARAM, questionId);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeMillis = schedule.getRepeat().getNextTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, timeMillis, pintent);
        }
    }

    public static void cancelAlarm(Context context, int questionId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pintent);
    }
}
