package feelings.helper;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;

import feelings.helper.repetition.WeeklyRepetition;

import static feelings.helper.DateTimeUtil.assertDayOfWeek;
import static feelings.helper.DateTimeUtil.assertDays;
import static feelings.helper.DateTimeUtil.assertTime;
import static feelings.helper.DateTimeUtil.assertToday;
import static feelings.helper.DateTimeUtil.assertTomorrow;
import static feelings.helper.DateTimeUtil.mockDayOfWeek;
import static feelings.helper.DateTimeUtil.mockDayOfWeekAndTime;
import static feelings.helper.DateTimeUtil.time;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertTrue;

public class WeeklyRepetitionTest {
    @AfterClass
    public static void tearDown() {
        setCurrentMillisSystem();
    }

    @Test
    public void testSameDayBeforeTime() {
        WeeklyRepetition repetition = new WeeklyRepetition(WEDNESDAY, time(12, 0));
        mockDayOfWeekAndTime(WEDNESDAY, 9, 15);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testSameDayAfterTime() {
        WeeklyRepetition repetition = new WeeklyRepetition(WEDNESDAY, time(12, 0));
        mockDayOfWeekAndTime(WEDNESDAY, 13, 15);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 7);
        assertDayOfWeek(nextTime, WEDNESDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowSeveralDaysDiff() {
        WeeklyRepetition repetition = new WeeklyRepetition(WEDNESDAY, time(12, 0));
        mockDayOfWeek(MONDAY);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 2);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowIsSundayAndTimeIsMonday() {
        WeeklyRepetition repetition = new WeeklyRepetition(MONDAY, time(12, 0));
        mockDayOfWeek(SUNDAY);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertDayOfWeek(nextTime, MONDAY);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testNowIsMondayAndTimeIsSunday() {
        WeeklyRepetition repetition = new WeeklyRepetition(SUNDAY, time(12, 0));
        mockDayOfWeek(MONDAY);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertDays(nextTime, 6);
        assertDayOfWeek(nextTime, SUNDAY);
        assertTime(12, 0, nextTime);
    }
}
