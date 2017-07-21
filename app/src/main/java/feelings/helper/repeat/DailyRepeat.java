package feelings.helper.repeat;

import android.content.Context;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.Iterator;
import java.util.TreeSet;

import feelings.helper.R;
import feelings.helper.util.TextUtil;

public class DailyRepeat extends AbstractRepeat {
    static final String DAILY = "d";

    /**
     * Set of times in each day
     */
    private final TreeSet<LocalTime> times;

    public DailyRepeat(TreeSet<LocalTime> times) {
        this.times = times;
    }

    /**
     * @param asString String from toDbString()
     */
    DailyRepeat(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repeat.");
        }
        String[] arr = asString.split(",");
        times = new TreeSet<>();
        for (String time : arr) {
            times.add(LocalTime.parse(time, TIME_FORMATTER));
        }
    }

    @Override
    public String toDbString() {
        TreeSet<String> timesStrings = new TreeSet<>();
        for (LocalTime time : times) {
            timesStrings.add(time.format(TIME_FORMATTER));
        }
        return TextUtil.join(",", timesStrings);
    }

    @Override
    public String toHumanReadableString(Context context) {
        TreeSet<String> timesStrings = new TreeSet<>();
        for (LocalTime time : times) {
            timesStrings.add(time.format(TIME_FORMATTER));
        }
        return String.format(context.getString(R.string.daily),
                TextUtil.join(", ", timesStrings));
    }

    @Override
    public String getType() {
        return DAILY;
    }

    @Override
    public LocalDateTime getNextTime() {
        if (times.isEmpty()) {
            throw new RuntimeException("Times should not be empty.");
        }

        LocalTime now = LocalTime.now(clock);
        LocalTime first = times.first();
        LocalTime last = times.last();

        // if now is after last, then the next time should be tomorrow
        if (now.isAfter(last)) {
            return todayAt(first).plusDays(1);
        }

        Iterator<LocalTime> iterator = times.iterator();
        LocalTime iter = iterator.next();
        while (iterator.hasNext() && iter.isBefore(now)) {
            iter = iterator.next();
        }

        return todayAt(iter);
    }
}
