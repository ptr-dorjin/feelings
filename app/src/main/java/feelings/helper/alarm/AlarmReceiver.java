package feelings.helper.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.app.NotificationCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feelings.helper.R;
import feelings.helper.question.QuestionService;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;
import feelings.helper.ui.answer.AnswerActivity;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

public class AlarmReceiver extends BroadcastReceiver {

    private static final Logger log = LoggerFactory.getLogger(AlarmReceiver.class);
    public static final int SPLIT_INDEX = 30;

    @Override
    public void onReceive(Context context, Intent intent) {
        int questionId = intent.getIntExtra(QUESTION_ID_PARAM, -1);
        log.info("Received for question {}", questionId);

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
            log.error("No schedule in the DB for question {}. Next alarm has not been set.", questionId);
        }
    }

    private Notification buildNotification(Context context, int questionId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Pair<String, String> splittedQuestionText = splitQuestionText(context, questionId);
        builder.setContentTitle(splittedQuestionText.first)
                .setContentText(splittedQuestionText.second)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

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

    private Pair<String, String> splitQuestionText(Context context, int questionId) {
        String questionText = QuestionService.getQuestionText(context, questionId);
        if (questionText == null) {
            return new Pair<>(context.getString(R.string.app_name), null);
        }
        if (questionText.length() <= SPLIT_INDEX) {
            return new Pair<>(questionText, null);
        }
        int lastSpace = questionText.lastIndexOf(" ", SPLIT_INDEX);
        if (lastSpace == -1) {
            return new Pair<>(questionText, null);
        }
        return new Pair<>(questionText.substring(0, lastSpace), questionText.substring(lastSpace + 1));
    }
}
