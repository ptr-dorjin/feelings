package feelings.helper.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;
import feelings.helper.settings.Settings;
import feelings.helper.settings.SettingsStore;

public class AlarmReceiver extends BroadcastReceiver {

    static final String QUESTION_ID = "question-id";

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int questionId = intent.getIntExtra(QUESTION_ID, 0);
        // 1. show notification
        try {
            WakeLocker.acquire(context);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(questionId, buildNotification(context, questionId));
        } finally {
            WakeLocker.release();
        }

        // 2. set the next alarm
        Settings settings = SettingsStore.getSettings(context, questionId);
        if (settings != null) {
            AlarmService.setAlarm(context, settings);
        } else {
            Log.e(TAG, "No settings in the DB with id=" + questionId + ". Next alarm has not been set.");
        }
    }

    private Notification buildNotification(Context context, int questionId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(QuestionService.getQuestionText(context, questionId));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else {
            //noinspection deprecation
            return builder.getNotification();
        }
    }
}
