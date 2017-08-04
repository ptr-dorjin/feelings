package feelings.helper.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showLong(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}