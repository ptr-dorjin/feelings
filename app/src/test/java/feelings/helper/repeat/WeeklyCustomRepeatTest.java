package feelings.helper.repeat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;

import java.util.TreeSet;

import static feelings.helper.TestDateTimeUtil.assertDayOfWeek;
import static feelings.helper.TestDateTimeUtil.assertDays;
import static feelings.helper.TestDateTimeUtil.assertFuture;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeekAndTime;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.threeten.bp.DayOfWeek.FRIDAY;
import static org.threeten.bp.DayOfWeek.MONDAY;
import static org.threeten.bp.DayOfWeek.SATURDAY;
import static org.threeten.bp.DayOfWeek.SUNDAY;
import static org.threeten.bp.DayOfWeek.THURSDAY;
import static org.threeten.bp.DayOfWeek.TUESDAY;
import static org.threeten.bp.DayOfWeek.WEDNESDAY;
import static org.threeten.bp.LocalTime.of;

public class WeeklyCustomRepeatTest {
    @Test
    public void testToString() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), of(21, 0));

        String asString = repeat.toString();
        WeeklyCustomRepeat fromString = new WeeklyCustomRepeat(asString);

        assertEquals("1,3,5;21:00", asString);
        assertEquals(asString, fromString.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new WeeklyCustomRepeat("");
    }

    @Test
    public void testOneOfTheDaysBeforeTime() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), of(21, 0));
        Clock clock = mockDayOfWeekAndTime(WEDNESDAY, 15, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertToday(nextTime, clock);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testOneOfTheDaysAfterTime() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), of(21, 0));
        Clock clock = mockDayOfWeekAndTime(WEDNESDAY, 21, 5, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 2, clock);
        assertDayOfWeek(nextTime, FRIDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowSeveralDaysDiff() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), of(21, 0));
        Clock clock = mockDayOfWeekAndTime(SATURDAY, 20, 5, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 2, clock);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowIsWeekendAndTimeIsWeekdays() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)), of(21, 0));
        Clock clock = mockDayOfWeekAndTime(SUNDAY, 10, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertTomorrow(nextTime, clock);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowIsWeekdayAndTimeIsWeekends() {
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(SATURDAY, SUNDAY)), of(11, 0));
        Clock clock = mockDayOfWeekAndTime(WEDNESDAY, 10, 0, repeat);

        LocalDateTime nextTime = repeat.getNextTime();

        assertFuture(nextTime, clock);
        assertDays(nextTime, 3, clock);
        assertDayOfWeek(nextTime, SATURDAY);
        assertTime(11, 0, nextTime);
    }
}
