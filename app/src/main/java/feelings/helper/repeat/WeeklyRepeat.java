package feelings.helper.repeat;

import android.content.Context;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import feelings.helper.R;
import feelings.helper.util.DateTimeUtil;
import feelings.helper.util.TextUtil;

public class WeeklyRepeat extends AbstractRepeat {
    /**
     * Day of week: from 1 to 7
     */
    private final DayOfWeek dayOfWeek;

    /**
     * Time of the day of week
     */
    private final LocalTime time;

    public WeeklyRepeat(DayOfWeek dayOfWeek, LocalTime time) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    /**
     * @param asString String from toDbString()
     */
    WeeklyRepeat(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repeat.");
        }
        String[] arr = asString.split(";");
        dayOfWeek = DayOfWeek.of(Integer.valueOf(arr[0]));
        time = LocalTime.parse(arr[1], TIME_FORMATTER);
    }

    @Override
    public String toDbString() {
        return dayOfWeek.getValue() + ";" + time.format(TIME_FORMATTER);
    }

    @Override
    public String toHumanReadableString(Context context) {
        return String.format(context.getString(R.string.weekly),
                DateTimeUtil.getDayOfWeekAsText(dayOfWeek),
                time.format(TIME_FORMATTER));
    }

    @Override
    public LocalDateTime getNextTime() {
        LocalDateTime now = LocalDateTime.now(clock);
        DayOfWeek dayOfWeekNow = now.getDayOfWeek();
        if (dayOfWeekNow == dayOfWeek) {
            // today is appointed day of week
            if (now.toLocalTime().isAfter(time)) {
                // time now is too late => add 1 week
                return todayAt(time).plusWeeks(1);
            } else {
                // appointed time not passed yet
                return todayAt(time);
            }
        } else {
            // today is another day of week
            int daysDiff = dayOfWeek.getValue() > dayOfWeekNow.getValue()
                    ? dayOfWeek.getValue() - dayOfWeekNow.getValue()
                    : 7 - dayOfWeekNow.getValue() + dayOfWeek.getValue();
            return todayAt(time).plusDays(daysDiff);
        }
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }
}
