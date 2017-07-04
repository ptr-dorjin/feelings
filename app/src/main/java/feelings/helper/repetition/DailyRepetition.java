package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Iterator;
import java.util.TreeSet;

public class DailyRepetition implements Repetition {

    /**
     * Set of times in each day
     */
    private TreeSet<LocalTime> times = new TreeSet<>();

    public DailyRepetition(TreeSet<LocalTime> times) {
        this.times = times;
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
