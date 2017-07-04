package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class WeeklyRepetition implements Repetition {

    /**
     * Day of week: from 1 to 7
     */
    private final int dayOfWeek;

    /**
     * Time of the day of week
     */
    private final LocalTime time;

    public WeeklyRepetition(int dayOfWeek, LocalTime time) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    @Override
    public DateTime getNextTime() {
        DateTime now = DateTime.now();
        int dayOfWeekNow = now.getDayOfWeek();
        if (dayOfWeekNow == dayOfWeek) {
            // today is appointed day of week
            if (now.toLocalTime().isAfter(time)) {
                // time now is too late => add 1 week
                return time.toDateTimeToday().plusWeeks(1);
            } else {
                // appointed time not passed yet
                return time.toDateTimeToday();
            }
        } else {
            // today is another day of week
            int daysDiff = dayOfWeek > dayOfWeekNow
                    ? dayOfWeek - dayOfWeekNow
                    : 7 - dayOfWeekNow + dayOfWeek;
            return time.toDateTimeToday().plusDays(daysDiff);
        }
    }
}
