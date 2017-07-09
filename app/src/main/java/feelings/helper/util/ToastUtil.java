package feelings.helper.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showError(final String message, final Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showShortMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}