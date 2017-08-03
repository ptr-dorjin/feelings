package feelings.helper.ui;

import android.view.View;
import android.view.ViewGroup;

public class UiUtil {

    public static void disableEnableControls(boolean enable, View v) {
        ViewGroup vg = (ViewGroup) v;
        vg.setEnabled(enable);
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, child);
            }
        }
    }

}
