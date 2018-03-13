package feelings.guide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.format.DateTimeFormatter;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static final DateTimeFormatter DB_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String ANSWER_LOG_DATE_DEFAULT_FORMAT = "d MMM yyyy";
    public static final String ANSWER_LOG_TIME_DEFAULT_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_PREFERENCE = "Feelings.Guide.Date.Format";
    public static final String TIME_FORMAT_PREFERENCE = "Feelings.Guide.Time.Format";

    public static String getDateTimeFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dateFormat = preferences.getString(DATE_FORMAT_PREFERENCE, ANSWER_LOG_DATE_DEFAULT_FORMAT);
        String timeFormat = preferences.getString(TIME_FORMAT_PREFERENCE, ANSWER_LOG_TIME_DEFAULT_FORMAT);
        return dateFormat + ", " + timeFormat;
    }
}
