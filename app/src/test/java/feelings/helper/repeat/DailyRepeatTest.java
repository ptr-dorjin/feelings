package feelings.helper.repeat;

import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.Collections;
import java.util.TreeSet;

import static feelings.helper.TestDateTimeUtil.assertFuture;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockTime;
import static org.junit.Assert.assertEquals;
import static org.threeten.bp.LocalTime.of;

public class DailyRepeatTest {

    private TreeSet<LocalTime> times = new TreeSet<>();

    @Before
    public void before() {
        times = new TreeSet<>();
    }

    @Test
    public void testToDbStringOne() {
        Collections.addAll(times, of(9, 0));
        testToDbString(times, "09:00");
    }

    @Test
    public void testToDbStringMultiple() {
        Collections.addAll(times, of(9, 0), of(12, 0), of(15, 0));
        testToDbString(times, "09:00,12:00,15:00");
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new DailyRepeat("");
    }

    private static void testToDbString(TreeSet<LocalTime> times, String expected) {
        DailyRepeat repeat = new DailyRepeat(times);

        String asString = repeat.toDbString();
        DailyRepeat fromString = new DailyRepeat(asString);

        assertEquals(expected, asString);
        assertEquals(asString, fromString.toDbString());
    }

    @Test
    public void testWithinBoundaries() {
        Collections.addAll(times, of(9, 0), of(12, 0), of(15, 0));
        DailyRepeat repeat = new DailyRepeat(times);
        Clock clock = mockTime(11, 15, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testBeforeMin() {
        Collections.addAll(times, of(9, 0), of(12, 0), of(15, 0));
        DailyRepeat repeat = new DailyRepeat(times);
        Clock clock = mockTime(5, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(9, 0, nextTime);
    }

    @Test
    public void testBeforeMaxIncluded() {
        Collections.addAll(times, of(9, 0), of(12, 0), of(15, 0));
        DailyRepeat repeat = new DailyRepeat(times);
        Clock clock = mockTime(14, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(15, 0, nextTime);
    }

    @Test
    public void testAfterMax() {
        Collections.addAll(times, of(9, 0), of(12, 0), of(15, 0));
        DailyRepeat repeat = new DailyRepeat(times);
        Clock clock = mockTime(16, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertTime(9, 0, nextTime);
    }
}
