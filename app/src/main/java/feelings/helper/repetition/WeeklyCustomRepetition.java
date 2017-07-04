package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.TreeSet;

public class WeeklyCustomRepetition implements Repetition {

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
