package feelings.helper.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    public static final String ANDROID_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";
    public static final String HTC_QUICKBOOT_POWERON = "com.htc.intent.action.QUICKBOOT_POWERON";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case ANDROID_QUICKBOOT_POWERON:
            case HTC_QUICKBOOT_POWERON:
                AlarmService.restartAll(context);
        }
    }
}
