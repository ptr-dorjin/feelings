package feelings.helper.repeat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import static feelings.helper.TestDateTimeUtil.assertDayOfWeek;
import static feelings.helper.TestDateTimeUtil.assertDays;
import static feelings.helper.TestDateTimeUtil.assertFuture;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeek;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeekAndTime;
import static org.junit.Assert.assertEquals;
import static org.threeten.bp.DayOfWeek.MONDAY;
import static org.threeten.bp.DayOfWeek.SUNDAY;
import static org.threeten.bp.DayOfWeek.TUESDAY;
import static org.threeten.bp.DayOfWeek.WEDNESDAY;

public class WeeklyRepeatTest {
    @Test
    public void testToString() {
        WeeklyRepeat repeat = new WeeklyRepeat(TUESDAY, LocalTime.of(8, 0));

        String asString = repeat.toString();
        WeeklyRepeat fromString = new WeeklyRepeat(asString);

        assertEquals("2;08:00", asString);
        assertEquals(asString, fromString.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new WeeklyRepeat("");
    }

    @Test
    public void testSameDayBeforeTime() {
        WeeklyRepeat repeat = new WeeklyRepeat(WEDNESDAY, LocalTime.of(12, 0));
        Clock clock = mockDayOfWeekAndTime(WEDNESDAY, 9, 15, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testSameDayAfterTime() {
        WeeklyRepeat repeat = new WeeklyRepeat(WEDNESDAY, LocalTime.of(12, 0));
        Clock clock = mockDayOfWeekAndTime(WEDNESDAY, 13, 15, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 7, clock);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowSeveralDaysDiff() {
        WeeklyRepeat repeat = new WeeklyRepeat(WEDNESDAY, LocalTime.of(12, 0));
        Clock clock = mockDayOfWeek(MONDAY, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 2, clock);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowIsSundayAndTimeIsMonday() {
        WeeklyRepeat repeat = new WeeklyRepeat(MONDAY, LocalTime.of(12, 0));
        Clock clock = mockDayOfWeek(SUNDAY, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowIsMondayAndTimeIsSunday() {
        WeeklyRepeat repeat = new WeeklyRepeat(SUNDAY, LocalTime.of(12, 0));
        Clock clock = mockDayOfWeek(MONDAY, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 6, clock);
        assertDayOfWeek(nextTime, SUNDAY);
        assertTime(12, 0, nextTime);
    }
}
