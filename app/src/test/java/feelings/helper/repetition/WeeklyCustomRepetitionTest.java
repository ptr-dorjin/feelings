package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeSet;

import static feelings.helper.TestDateTimeUtil.assertDayOfWeek;
import static feelings.helper.TestDateTimeUtil.assertDays;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeekAndTime;
import static feelings.helper.TestDateTimeUtil.time;
import static java.util.Arrays.asList;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeeklyCustomRepetitionTest {
    @AfterClass
    public static void tearDown() {
        setCurrentMillisSystem();
    }

    @Test
    public void testToString() {
        TreeSet<Integer> daysOfWeek = new TreeSet<>();
        Collections.addAll(daysOfWeek, 1, 3, 5);
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(daysOfWeek, time(21, 0));
        String asString = repetition.toString();
        WeeklyCustomRepetition fromString = new WeeklyCustomRepetition(asString);

        assertEquals("1,3,5;21:00", asString);
        assertEquals(asString, fromString.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new WeeklyCustomRepetition("");
    }

    @Test
    public void testOneOfTheDaysBeforeTime() {
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), time(21, 0));
        mockDayOfWeekAndTime(WEDNESDAY, 15, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testOneOfTheDaysAfterTime() {
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), time(21, 0));
        mockDayOfWeekAndTime(WEDNESDAY, 21, 5);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 2);
        assertDayOfWeek(nextTime, FRIDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowSeveralDaysDiff() {
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), time(21, 0));
        mockDayOfWeekAndTime(SATURDAY, 20, 5);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 2);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowIsWeekendAndTimeIsWeekdays() {
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(
                new TreeSet<>(asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)), time(21, 0));
        mockDayOfWeekAndTime(SUNDAY, 10, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testNowIsWeekdayAndTimeIsWeekends() {
        WeeklyCustomRepetition repetition = new WeeklyCustomRepetition(
                new TreeSet<>(asList(SATURDAY, SUNDAY)), time(11, 0));
        mockDayOfWeekAndTime(WEDNESDAY, 10, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 3);
        assertDayOfWeek(nextTime, SATURDAY);
        assertTime(11, 0, nextTime);
    }
}
