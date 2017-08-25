package feelings.helper.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import feelings.helper.R;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.helper.util.DateTimeUtil.LOG_FORMATTER;

public class AlarmService extends Service {
    /**
     * Used to pass schedule in two cases:
     * 1. from static methods to instance service method (onStartCommand())
     * 2. from onStartCommand to another thread (serviceHandler)
     */
    private static final String SCHEDULE = "schedule";
    private static final int FOREGROUND_NOTIFICATION_ID = 999999;

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

    private ServiceHandler serviceHandler;

    /**
     * Static convenient method for one Schedule. Starts self as a service
     */
    public static void setAlarm(Context context, Schedule schedule) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(SCHEDULE, schedule);
        context.startService(intent);
    }

    /**
     * Static convenient method for all Schedules. Starts self as a service
     */
    public static void restartAll(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        context.startService(intent);
    }

    /**
     * Static convenient method to cancel one Schedule.
     */
    public static void cancelAlarm(Context context, int questionId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, questionId, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pintent);
        log.info("question {}", questionId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceHandler = new ServiceHandler(thread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification());

        Schedule schedule = null;
        if (intent != null) {
            schedule = intent.getParcelableExtra(SCHEDULE);
            if (schedule == null) {
                log.debug("Schedule is null");
            }
        } else {
            log.debug("Intent is null");
        }

        //send schedule further to another thread
        Message msg = serviceHandler.obtainMessage();
        Bundle data = new Bundle();
        data.putParcelable(SCHEDULE, schedule);
        msg.setData(data);
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }

    // Handler that receives messages from the thread
    @Override
    public void onDestroy() {
        log.debug("Service destroyed");
    }

    private final class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            Schedule schedule = data.getParcelable(SCHEDULE);
            if (schedule != null) {
                log.debug("Set alarm for one schedule");
                setOneAlarm(schedule);
            } else {
                log.debug("Set alarms for all schedules");
                for (Schedule sch : ScheduleService.getAllSchedules(AlarmService.this)) {
                    if (sch.isOn()) {
                        setOneAlarm(sch);
                    }
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

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.foreground_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher);

        return (builder.build());
    }
}
