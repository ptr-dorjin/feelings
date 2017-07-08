package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;

import static feelings.helper.TestDateTimeUtil.assertDayOfWeek;
import static feelings.helper.TestDateTimeUtil.assertDays;
import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeek;
import static feelings.helper.TestDateTimeUtil.mockDayOfWeekAndTime;
import static feelings.helper.TestDateTimeUtil.time;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeeklyRepetitionTest {
    @AfterClass
    public static void tearDown() {
        setCurrentMillisSystem();
    }

    @Test
    public void testToString() {
        WeeklyRepetition repetition = new WeeklyRepetition(2, time(8, 0));
        String asString = repetition.toString();
        WeeklyRepetition fromString = new WeeklyRepetition(asString);

        assertEquals("2;08:00", asString);
        assertEquals(asString, fromString.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new WeeklyRepetition("");
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
