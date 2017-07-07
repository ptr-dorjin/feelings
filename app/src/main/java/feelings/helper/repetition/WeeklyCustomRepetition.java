package feelings.helper.repetition;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.TreeSet;

public class WeeklyCustomRepetition implements Repetition {
    static final String WEEKLY_CUSTOM = "wc";

    /**
     * Set of days of week
     */
    private final TreeSet<Integer> daysOfWeek;

    /**
     * Time of the days of week
     */
    private final LocalTime time;

    public WeeklyCustomRepetition(TreeSet<Integer> daysOfWeek, LocalTime time) {
        this.daysOfWeek = daysOfWeek;
        this.time = time;
    }

    WeeklyCustomRepetition(String asString) {
        String[] arr = asString.split(";");
        String[] days = arr[0].split(",");
        daysOfWeek = new TreeSet<>();
        for (String day : days) {
            daysOfWeek.add(Integer.valueOf(day));
        }
        time = TIME_FORMATTER.parseLocalTime(arr[1]);
    }

    @Override
    public String toString() {
        return TextUtils.join(",", daysOfWeek) + ";" + TIME_FORMATTER.print(time);
    }

    @Override
    public String getType() {
        return WEEKLY_CUSTOM;
    }

    @Override
    public DateTime getNextTime() {
        DateTime now = DateTime.now();
        int dayOfWeekNow = now.getDayOfWeek();
        if (daysOfWeek.contains(dayOfWeekNow)) {
            // today is one the appointed days of week
            if (now.toLocalTime().isAfter(time)) {
                // time now is too late => find next day
                return findNextDay();
            } else {
                // appointed time not passed yet
                return time.toDateTimeToday();
            }
        } else {
            // today is not one of the appointed days of week
            return findNextDay();
        }
    }

    private DateTime findNextDay() {
        Integer nextDayInThisWeek = daysOfWeek.ceiling(DateTime.now().getDayOfWeek() + 1);
        if (nextDayInThisWeek == null) {
            // no future days appointed within this week => get first day on the next week
            return time.toDateTimeToday().withDayOfWeek(daysOfWeek.first()).plusWeeks(1);
        } else {
            // have future day appointed within this week
            return time.toDateTimeToday().withDayOfWeek(nextDayInThisWeek);
        }
    }
}
