package feelings.helper.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import feelings.helper.repetition.Repetition;
import feelings.helper.repetition.RepetitionSettingService;

public class AlarmService {

    public static void setAlarm(Context context, int questionId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.QUESTION_ID, questionId);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //todo need to check that repetition can be null?
        Repetition repetition = RepetitionSettingService.getRepetition(questionId);
        long timeMillis = repetition.getNextTime().getMillis();

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
