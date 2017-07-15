package feelings.helper;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class FeelingsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
