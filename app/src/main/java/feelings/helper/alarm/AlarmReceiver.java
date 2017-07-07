package feelings.helper.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;
import feelings.helper.settings.SettingsStore;

public class AlarmReceiver extends BroadcastReceiver {

    static final String QUESTION_ID = "question-id";

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
        AlarmService.setAlarm(context, SettingsStore.getSettings(context, questionId));
    }

    private Notification buildNotification(Context context, int questionId) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(DateTime.now().toString());
        builder.setContentText(QuestionService.getQuestionText(questionId));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }
}
