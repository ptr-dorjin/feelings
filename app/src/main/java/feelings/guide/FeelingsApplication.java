package feelings.guide;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.jakewharton.threetenabp.AndroidThreeTen;

import feelings.guide.profile.LocaleUtil;

public class FeelingsApplication extends Application {

    public static final String QUESTION_ID_PARAM = "question_id";
    public static final String ANSWER_ID_PARAM = "answer_id";
    public static final int SETTINGS_REQUEST_CODE = 777;
    public static final int UPDATE_ANSWER_REQUEST_CODE = 102;
    public static final String REFRESH_QUESTIONS_RESULT_KEY = "should-refresh-questions";
    public static final String REFRESH_ANSWER_LOG_RESULT_KEY = "should-refresh-answer-log";
    public static final String UPDATED_ANSWER_ID_RESULT_KEY = "updated-answer-id";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.INSTANCE.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtil.INSTANCE.setLocale(this);
    }
}

