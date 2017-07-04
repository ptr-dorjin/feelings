package feelings.helper.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;

public class AlarmReceiver extends BroadcastReceiver {

    public static String QUESTION_ID = "question-id";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            WakeLocker.acquire(context);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int questionId = intent.getIntExtra(QUESTION_ID, 0);
            nm.notify(questionId, buildNotification(context, questionId));
        } finally {
            WakeLocker.release();
        }
    }

    private Notification buildNotification(Context context, int questionId) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(DateTime.now().toString());
        builder.setContentText(QuestionService.getQuestionText(questionId));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }
}
