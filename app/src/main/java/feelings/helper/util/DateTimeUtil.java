package feelings.helper.util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import feelings.helper.profile.Profile;

public class DateTimeUtil {

    public static String getDayOfWeekAsText(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Profile.getLocale());
    }
}
