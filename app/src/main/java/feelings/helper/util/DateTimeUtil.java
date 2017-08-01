package feelings.helper.util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.List;

import feelings.helper.profile.Profile;

public class DateTimeUtil {

    public static String getDayOfWeekAsText(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Profile.getLocale());
    }

    public static List<String> getAllDaysOfWeekLocalized() {
        List<String> allDays = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            allDays.add(dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Profile.getLocale()));
        }
        return allDays;
    }
}
