package feelings.guide;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class FeelingsApplication extends Application {

    public static final String QUESTION_ID_PARAM = "question_id";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
