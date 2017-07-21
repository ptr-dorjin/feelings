package feelings.helper.repeat;

import android.content.Context;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import feelings.helper.R;
import feelings.helper.util.TextUtil;

import static feelings.helper.util.DateTimeUtil.getDayOfWeekAsText;

public class WeeklyCustomRepeat extends AbstractRepeat {
    static final String WEEKLY_CUSTOM = "wc";

    /**
     * Set of days of week
     */
    private final TreeSet<DayOfWeek> daysOfWeek;

    /**
     * Time of the days of week
     */
    private final LocalTime time;

    public WeeklyCustomRepeat(TreeSet<DayOfWeek> daysOfWeek, LocalTime time) {
        this.daysOfWeek = daysOfWeek;
        this.time = time;
    }

    /**
     * @param asString String from toDbString()
     */
    WeeklyCustomRepeat(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repeat.");
        }
        String[] arr = asString.split(";");
        String[] days = arr[0].split(",");
        daysOfWeek = new TreeSet<>();
        for (String day : days) {
            daysOfWeek.add(DayOfWeek.of(Integer.valueOf(day)));
        }
        time = LocalTime.parse(arr[1], TIME_FORMATTER);
    }

    @Override
    public String toDbString() {
        TreeSet<Integer> daysOfWeekInts = new TreeSet<>();
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            daysOfWeekInts.add(dayOfWeek.getValue());
        }
        return TextUtil.join(",", daysOfWeekInts) + ";" + time.format(TIME_FORMATTER);
    }

    @Override
    public String toHumanReadableString(Context context) {
        List<String> daysOfWeekStrings = new ArrayList<>();
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            daysOfWeekStrings.add(getDayOfWeekAsText(dayOfWeek));
        }
        return String.format(context.getString(R.string.weekly_custom),
                TextUtil.join(", ", daysOfWeekStrings),
                time.format(TIME_FORMATTER));
    }

    @Override
    public String getType() {
        return WEEKLY_CUSTOM;
    }

    @Override
    public LocalDateTime getNextTime() {
        LocalDateTime now = LocalDateTime.now(clock);
        DayOfWeek dayOfWeekNow = now.getDayOfWeek();
        if (daysOfWeek.contains(dayOfWeekNow)) {
            // today is one the appointed days of week
            if (now.toLocalTime().isAfter(time)) {
                // time now is too late => find next day
                return findNextDay();
            } else {
                // appointed time not passed yet
                return todayAt(time);
            }
        } else {
            // today is not one of the appointed days of week
            return findNextDay();
        }
    }

    private LocalDateTime findNextDay() {
        DayOfWeek dayOfWeekNow = LocalDateTime.now(clock).getDayOfWeek();
        DayOfWeek nextDayOnThisWeek = null;
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            if (dayOfWeek.compareTo(dayOfWeekNow) > 0) {
                nextDayOnThisWeek = dayOfWeek;
                break;
            }
        }

        if (nextDayOnThisWeek == null) {
            // no future days appointed within this week => get first day on the next week
            return todayAt(time).with(daysOfWeek.first()).plusWeeks(1);
        } else {
            // have future day appointed within this week
            return todayAt(time).with(nextDayOnThisWeek);
        }
    }
}
