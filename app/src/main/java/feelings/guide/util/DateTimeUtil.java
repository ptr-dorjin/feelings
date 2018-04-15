package feelings.guide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.format.DateTimeFormatter;

import static feelings.guide.ui.settings.SettingsFragment.DATE_FORMAT_KEY;
import static feelings.guide.ui.settings.SettingsFragment.TIME_FORMAT_KEY;
import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static final DateTimeFormatter DB_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String ANSWER_LOG_DATE_DEFAULT_FORMAT = "d MMM yyyy";
    private static final String ANSWER_LOG_TIME_DEFAULT_FORMAT = "HH:mm";

    public static String getDateTimeFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dateFormat = preferences.getString(DATE_FORMAT_KEY, ANSWER_LOG_DATE_DEFAULT_FORMAT);
        String timeFormat = preferences.getString(TIME_FORMAT_KEY, ANSWER_LOG_TIME_DEFAULT_FORMAT);
        return dateFormat + ", " + timeFormat;
    }
}
