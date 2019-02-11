package feelings.guide.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import feelings.guide.profile.LocaleUtil;

import static android.content.pm.PackageManager.GET_META_DATA;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetTitle();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.setLocale(base));
    }

    private void resetTitle() {
        try {
            int label = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA).labelRes;
            if (label != 0) {
                setTitle(label);
            }
        } catch (PackageManager.NameNotFoundException ignore) {
            //do nothing
        }
    }
}
