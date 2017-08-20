package feelings.helper.alarm;

import android.content.Context;
import android.os.PowerManager;

class WakeLocker {
    private static final String APP_TAG = "FeelingsHelper";
    private static PowerManager.WakeLock wakeLock;

    static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, APP_TAG);
        wakeLock.acquire();
    }

    static void release() {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
    }
}
