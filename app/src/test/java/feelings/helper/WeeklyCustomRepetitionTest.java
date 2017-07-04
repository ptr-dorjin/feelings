package feelings.helper;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.TreeSet;

import feelings.helper.repetition.WeeklyCustomRepetition;

import static feelings.helper.DateTimeUtil.assertDayOfWeek;
import static feelings.helper.DateTimeUtil.assertDays;
import static feelings.helper.DateTimeUtil.assertTime;
import static feelings.helper.DateTimeUtil.assertToday;
import static feelings.helper.DateTimeUtil.assertTomorrow;
import static feelings.helper.DateTimeUtil.mockDayOfWeekAndTime;
import static feelings.helper.DateTimeUtil.time;
import static java.util.Arrays.asList;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertTrue;

public class WeeklyCustomRepetitionTest {
    @AfterClass
    public static void tearDown() {
        setCurrentMillisSystem();
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
