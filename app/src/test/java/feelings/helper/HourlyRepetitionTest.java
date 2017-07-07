package feelings.helper;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;

import feelings.helper.repetition.HourlyRepetition;

import static feelings.helper.TestDateTimeUtil.assertTime;
import static feelings.helper.TestDateTimeUtil.assertToday;
import static feelings.helper.TestDateTimeUtil.assertTomorrow;
import static feelings.helper.TestDateTimeUtil.mockTime;
import static feelings.helper.TestDateTimeUtil.time;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertTrue;

public class HourlyRepetitionTest {
    @AfterClass
    public static void tearDown() {
        setCurrentMillisSystem();
    }

    @Test
    public void testNowWithinBoundaries() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(20, 0));
        mockTime(12, 15);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(14, 0, nextTime);
    }

    @Test
    public void testNowBeforeStart() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(20, 0));
        mockTime(5, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testNowBeforeEndIncluded() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(20, 0));
        mockTime(19, 55);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(20, 0, nextTime);
    }

    @Test
    public void testNowAfterEnd() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(20, 0));
        mockTime(20, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testNowBeforeEndExcluded() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(19, 0));
        mockTime(18, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartEqualsEndNowBefore() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(8, 0));
        mockTime(7, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartEqualsEndNowAfter() {
        HourlyRepetition repetition = new HourlyRepetition(2, time(8, 0), time(8, 0));
        mockTime(9, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertTime(8, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowBeforeStart() {
        HourlyRepetition repetition = new HourlyRepetition(1, time(21, 0), time(8, 0));
        mockTime(18, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(21, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowInsideToday() {
        HourlyRepetition repetition = new HourlyRepetition(1, time(21, 0), time(8, 0));
        mockTime(22, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(23, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowInsideTomorrow() {
        HourlyRepetition repetition = new HourlyRepetition(1, time(21, 0), time(8, 0));
        mockTime(2, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(3, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowBeforeMidnight() {
        HourlyRepetition repetition = new HourlyRepetition(1, time(21, 0), time(8, 0));
        mockTime(23, 45);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertTime(0, 0, nextTime);
    }

    @Test
    public void testStartAfterEndNowAfterEnd() {
        HourlyRepetition repetition = new HourlyRepetition(1, time(21, 0), time(8, 0));
        mockTime(10, 45);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(21, 0, nextTime);
    }
}
