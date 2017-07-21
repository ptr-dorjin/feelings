package feelings.helper.repeat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import feelings.helper.R;
import feelings.helper.util.TextUtil;

public class HourlyRepeat extends AbstractRepeat {
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

    public HourlyRepeat(int interval, LocalTime start, LocalTime end) {
        this.interval = interval;
        this.start = start;
        this.end = end;
    }

    /**
     * @param asString String from toDbString()
     */
    HourlyRepeat(String asString) {
        if (TextUtil.isEmpty(asString)) {
            throw new RuntimeException("Incorrect string value of the repeat.");
        }
        String[] arr = asString.split(";");
        interval = Integer.valueOf(arr[0]);
        start = LocalTime.parse(arr[1], TIME_FORMATTER);
        end = LocalTime.parse(arr[2], TIME_FORMATTER);
    }

    @Override
    public String toDbString() {
        return interval + ";" + start.format(TIME_FORMATTER) + ";" + end.format(TIME_FORMATTER);
    }

    public String toHumanReadableString(Context context) {
        return String.format(context.getString(R.string.hourly),
                TextUtil.getPluralText(interval,
                        context.getString(R.string.every1),
                        context.getString(R.string.every2),
                        context.getString(R.string.every5)),
                interval,
                TextUtil.getPluralText(interval,
                        context.getString(R.string.hour1),
                        context.getString(R.string.hour2),
                        context.getString(R.string.hour5)),
                start.format(TIME_FORMATTER), end.format(TIME_FORMATTER));
    }

    @Override
    public String getType() {
        return HOURLY;
    }

    @Override
    public LocalDateTime getNextTime() {
        return ! start.isAfter(end)
                ? startBeforeEnd()
                : startAfterEnd();
    }

    @NonNull
    private LocalDateTime startBeforeEnd() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime low = todayAt(start);
        LocalDateTime high = todayAt(end);

        // 0 <= now <= low
        if (!low.isBefore(now)) {
            return low;
        }
        // low < now <= high
        else if (!high.isBefore(now)) {
            LocalDateTime next = getNext(low, high);
            return next != null ? next : low.plusDays(1);
        }
        // high < now <= 24
        else {
            return low.plusDays(1);
        }
    }

    @NonNull
    private LocalDateTime startAfterEnd() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime low = todayAt(end);
        LocalDateTime high = todayAt(start);

        // 0 <= now <= low
        if (!low.isBefore(now)) {
            LocalDateTime next = getNext(LocalDate.now(clock).atStartOfDay(), low);
            return next != null ? next : high;
        }
        // low < now < high
        else if (high.isAfter(now)) {
            return high;
        }
        // high <= now <= 24
        else {
            LocalDateTime next = getNext(high, low.plusDays(1));
            return next != null ? next : high.plusDays(1);
        }
    }

    @Nullable
    private LocalDateTime getNext(LocalDateTime from, LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now(clock);

        LocalDateTime iter = from;
        while (!iter.isAfter(to) && iter.isBefore(now)) {
            iter = iter.plusHours(interval);
        }

        if (iter.isAfter(to)) {
            return null;
        }

        return iter;
    }
}
