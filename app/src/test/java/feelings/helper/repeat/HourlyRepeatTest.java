package feelings.helper.repeat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;

import static feelings.helper.TestDateTimeUtil.assertFuture;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockTime;
import static org.junit.Assert.assertEquals;
import static org.threeten.bp.LocalTime.of;

public class HourlyRepeatTest {
    @Test
    public void testToString() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));

        String asString = repeat.toString();
        HourlyRepeat fromString = new HourlyRepeat(asString);

        assertEquals("2;08:00;20:00", asString);
        assertEquals(asString, fromString.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new HourlyRepeat("");
    }

    @Test
    public void testNowWithinBoundaries() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));
        Clock clock = mockTime(12, 15, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(14, 0, nextTime);
    }

    @Test
    public void testNowBeforeStart() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));
        Clock clock = mockTime(5, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testNowBeforeEndIncluded() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));
        Clock clock = mockTime(19, 55, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(20, 0, nextTime);
    }

    @Test
    public void testNowAfterEnd() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));
        Clock clock = mockTime(20, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testNowBeforeEndExcluded() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(19, 0));
        Clock clock = mockTime(18, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartEqualsEndNowBefore() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(8, 0));
        Clock clock = mockTime(7, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartEqualsEndNowAfter() {
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(8, 0));
        Clock clock = mockTime(9, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowBeforeStart() {
        HourlyRepeat repeat = new HourlyRepeat(1, of(21, 0), of(8, 0));
        Clock clock = mockTime(18, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowInsideToday() {
        HourlyRepeat repeat = new HourlyRepeat(1, of(21, 0), of(8, 0));
        Clock clock = mockTime(22, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(23, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowInsideTomorrow() {
        HourlyRepeat repeat = new HourlyRepeat(1, of(21, 0), of(8, 0));
        Clock clock = mockTime(2, 30, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(3, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowBeforeMidnight() {
        HourlyRepeat repeat = new HourlyRepeat(1, of(21, 0), of(8, 0));
        Clock clock = mockTime(23, 45, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertTime(0, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowAfterEnd() {
        HourlyRepeat repeat = new HourlyRepeat(1, of(21, 0), of(8, 0));
        Clock clock = mockTime(10, 45, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertTime(21, 0, nextTime);
    }
}
