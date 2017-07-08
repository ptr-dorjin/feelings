package feelings.helper.repetition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import feelings.helper.util.TextUtil;

public class HourlyRepetition implements Repetition {
    static final String HOURLY = "h";

    /**
     * Interval in hours
     */
    private final int interval;

    /**
     * Starting time of day
     */
    private final LocalTime start;

    /**
     * Ending time of day
     */
    private final LocalTime end;

    public HourlyRepetition(int interval, LocalTime start, LocalTime end) {
        this.interval = interval;
        this.start = start;
        this.end = end;
    }

    /**
     * @param asString String from toString()
     */
    HourlyRepetition(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repetition.");
        }
        String[] arr = asString.split(";");
        interval = Integer.valueOf(arr[0]);
        start = TIME_FORMATTER.parseLocalTime(arr[1]);
        end = TIME_FORMATTER.parseLocalTime(arr[2]);
    }

    @Override
    public String toString() {
        return interval + ";" + TIME_FORMATTER.print(start) + ";" + TIME_FORMATTER.print(end);
    }

    @Override
    public String getType() {
        return HOURLY;
    }

    @Override
    public DateTime getNextTime() {
        return ! start.isAfter(end)
                ? startBeforeEnd()
                : startAfterEnd();
    }

    @NonNull
    private DateTime startBeforeEnd() {
        DateTime low = start.toDateTimeToday();
        DateTime high = end.toDateTimeToday();

        // 0 <= now <= low
        if (!low.isBeforeNow()) {
            return low;
        }
        // low < now <= high
        else if (!high.isBeforeNow()) {
            DateTime next = getNext(low, high);
            return next != null ? next : low.plusDays(1);
        }
        // high < now <= 24
        else {
            return low.plusDays(1);
        }
    }

    @NonNull
    private DateTime startAfterEnd() {
        DateTime low = end.toDateTimeToday();
        DateTime high = start.toDateTimeToday();

        // 0 <= now <= low
        if (!low.isBeforeNow()) {
            DateTime next = getNext(DateTime.now().withTimeAtStartOfDay(), low);
            return next != null ? next : high;
        }
        // low < now < high
        else if (high.isAfterNow()) {
            return high;
        }
        // high <= now <= 24
        else {
            DateTime next = getNext(high, low.plusDays(1));
            return next != null ? next : high.plusDays(1);
        }
    }

    @Nullable
    private DateTime getNext(DateTime from, DateTime to) {
        DateTime now = DateTime.now();

        DateTime iter = from;
        while (!iter.isAfter(to) && iter.isBefore(now)) {
            iter = iter.plusHours(interval);
        }

        if (iter.isAfter(to)) {
            return null;
        }

        return iter;
    }
}
