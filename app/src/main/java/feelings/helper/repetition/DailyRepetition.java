package feelings.helper.repetition;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Iterator;
import java.util.TreeSet;

public class DailyRepetition implements Repetition {
    static final String DAILY = "d";

    /**
     * Set of times in each day
     */
    private final TreeSet<LocalTime> times;

    public DailyRepetition(TreeSet<LocalTime> times) {
        this.times = times;
    }

    /**
     * @param asString String from toString()
     */
    DailyRepetition(String asString) {
        String[] arr = asString.split(",");
        times = new TreeSet<>();
        for (String time : arr) {
            times.add(TIME_FORMATTER.parseLocalTime(time));
        }
    }

    @Override
    public String toString() {
        TreeSet<String> timesStrings = new TreeSet<>();
        for (LocalTime time : times) {
            timesStrings.add(TIME_FORMATTER.print(time));
        }
        return TextUtils.join(",", timesStrings);
    }

    @Override
    public String getType() {
        return DAILY;
    }

    @Override
    public DateTime getNextTime() {
        if (times.isEmpty()) {
            throw new RuntimeException("Times should not be empty.");
        }

        LocalTime now = LocalTime.now();
        LocalTime first = times.first();
        LocalTime last = times.last();

        // if now is after last, then the next time should be tomorrow
        if (now.isAfter(last)) {
            return first.toDateTimeToday().plusDays(1);
        }

        Iterator<LocalTime> iterator = times.iterator();
        LocalTime iter = iterator.next();
        while (iterator.hasNext() && iter.isBefore(now)) {
            iter = iterator.next();
        }

        return iter.toDateTimeToday();
    }
}
