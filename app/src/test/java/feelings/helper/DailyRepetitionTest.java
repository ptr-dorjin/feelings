package feelings.helper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeSet;

import feelings.helper.repetition.DailyRepetition;

import static feelings.helper.DateTimeUtil.assertTime;
import static feelings.helper.DateTimeUtil.assertToday;
import static feelings.helper.DateTimeUtil.assertTomorrow;
import static feelings.helper.DateTimeUtil.mockDateTime;
import static feelings.helper.DateTimeUtil.time;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertTrue;

public class DailyRepetitionTest {

    private TreeSet<LocalTime> times = new TreeSet<>();

    @AfterClass
    public static void afterClass() {
        setCurrentMillisSystem();
    }

    @Before
    public void before() {
        times = new TreeSet<>();
    }

    @Test
    public void testWithinBoundaries() {
        Collections.addAll(times, time(9, 0), time(12, 0), time(15, 0));
        DailyRepetition repetition = new DailyRepetition(times);
        mockDateTime(11, 15);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(12, 0, nextTime);
    }

    @Test
    public void testBeforeMin() {
        Collections.addAll(times, time(9, 0), time(12, 0), time(15, 0));
        DailyRepetition repetition = new DailyRepetition(times);
        mockDateTime(5, 0);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(9, 0, nextTime);
    }

    @Test
    public void testBeforeMaxIncluded() {
        Collections.addAll(times, time(9, 0), time(12, 0), time(15, 0));
        DailyRepetition repetition = new DailyRepetition(times);
        mockDateTime(14, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertToday(nextTime);
        assertTime(15, 0, nextTime);
    }

    @Test
    public void testAfterMax() {
        Collections.addAll(times, time(9, 0), time(12, 0), time(15, 0));
        DailyRepetition repetition = new DailyRepetition(times);
        mockDateTime(16, 30);

        DateTime nextTime = repetition.getNextTime();

        assertTrue(nextTime.isAfterNow());
        assertTomorrow(nextTime);
        assertTime(9, 0, nextTime);
    }
}
