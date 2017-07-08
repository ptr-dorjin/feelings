package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import feelings.helper.util.TextUtil;

public class WeeklyRepetition implements Repetition {
    static final String WEEKLY = "w";

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

    /**
     * @param asString String from toString()
     */
    WeeklyRepetition(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repetition.");
        }
        String[] arr = asString.split(";");
        dayOfWeek = Integer.valueOf(arr[0]);
        time = TIME_FORMATTER.parseLocalTime(arr[1]);
    }

    @Override
    public String toString() {
        return dayOfWeek + ";" + TIME_FORMATTER.print(time);
    }

    @Override
    public String getType() {
        return WEEKLY;
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
