package feelings.guide;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.jakewharton.threetenabp.AndroidThreeTen;

import feelings.guide.profile.LocaleUtil;

public class FeelingsApplication extends Application {

    public static final String QUESTION_ID_PARAM = "question_id";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtil.setLocale(this);
    }
}

