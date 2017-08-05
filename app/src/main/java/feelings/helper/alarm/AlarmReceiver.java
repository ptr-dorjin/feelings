package feelings.helper.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import feelings.helper.R;
import feelings.helper.question.QuestionService;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;
import feelings.helper.ui.answers.AnswerActivity;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int questionId = intent.getIntExtra(QUESTION_ID_PARAM, 0);
        // 1. show notification
        try {
            WakeLocker.acquire(context);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(questionId, buildNotification(context, questionId));
        } finally {
            WakeLocker.release();
        }

        // 2. set the next alarm
        Schedule schedule = ScheduleService.getSchedule(context, questionId);
        if (schedule != null) {
            AlarmService.setAlarm(context, schedule);
        } else {
            Log.e(TAG, "No schedule in the DB with id=" + questionId + ". Next alarm has not been set.");
        }
    }

    private Notification buildNotification(Context context, int questionId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(QuestionService.getQuestionText(context, questionId));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, AnswerActivity.class);
        intent.putExtra(QUESTION_ID_PARAM, questionId);
        PendingIntent pintent = PendingIntent.getActivity(context, questionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pintent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else {
            //noinspection deprecation
            return builder.getNotification();
        }
    }
}
