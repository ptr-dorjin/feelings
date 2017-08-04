package feelings.helper.util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.List;

import feelings.helper.profile.Profile;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static final DateTimeFormatter FULL_FORMATTER = ofPattern("dd.MM HH:mm");
    public static final DateTimeFormatter DB_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIME_FORMATTER = ofPattern("HH:mm");
    public static final DateTimeFormatter HOUR_FORMATTER = ofPattern("HH");
    public static final DateTimeFormatter MINUTE_FORMATTER = ofPattern("mm");

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
