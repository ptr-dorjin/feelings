package feelings.helper;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import feelings.helper.alarm.AlarmService;

public class FeelingsApplication extends Application {

    public static final String QUESTION_ID_PARAM = "question_id";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        configureLogback();

        final Logger log = LoggerFactory.getLogger(FeelingsApplication.class);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                log.error("Uncaught exception", e);
            }
        });

        AlarmService.restartAll(this);
    }

    private void configureLogback() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // Enable debugging to show rollover status
        OnConsoleStatusListener.addNewInstanceToContext(context);

        LogcatAppender logcatAppender = setUpLogcatAppender(context);
        RollingFileAppender<ILoggingEvent> rollingFileAppender = setUpRollingFileAppender(context);

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ALL);
        root.addAppender(logcatAppender);
        if (rollingFileAppender != null) {
            root.addAppender(rollingFileAppender);
        }
    }

    @NonNull
    private LogcatAppender setUpLogcatAppender(LoggerContext context) {
        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(context);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%msg%n");
        encoder.setContext(context);
        encoder.start();

        logcatAppender.setEncoder(encoder);
        logcatAppender.start();
        return logcatAppender;
    }

    @Nullable
    private RollingFileAppender<ILoggingEvent> setUpRollingFileAppender(LoggerContext context) {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir == null) {
            Log.e("FeelingsApp", "configureLogback: Couldn't get external files dir");
            return null;
        }
        final String LOG_DIR = externalFilesDir.getAbsolutePath();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);

        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
        rollingFileAppender.setFile(LOG_DIR + "/log.txt");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setFileNamePattern(LOG_DIR + "/log.%d.txt");
        rollingPolicy.setMaxHistory(7); // no more than 7 rollover files (delete oldest)
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger{35}.%M - %msg%n");
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();
        return rollingFileAppender;
    }
}
