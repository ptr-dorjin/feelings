package feelings.helper.schedule;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalTime;

import java.util.Collection;

import feelings.helper.repeat.HourlyRepeat;

import static feelings.helper.repeat.RepeatType.HOURLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ScheduleStoreTest {
    private static final HourlyRepeat REPEAT = new HourlyRepeat(2, LocalTime.of(8, 0), LocalTime.of(20, 0));
    private static final int QUESTION_ID = 100;
    private static final int QUESTION_ID_2 = 101;
    private static final int QUESTION_ID_3 = 102;

    @BeforeClass
    public static void beforeClass() {
        ScheduleStore.deleteAll(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.delete(context, QUESTION_ID);
        ScheduleStore.delete(context, QUESTION_ID_2);
        ScheduleStore.delete(context, QUESTION_ID_3);
    }

    @Test
    public void testCreate() {
        Context context = InstrumentationRegistry.getTargetContext();
        Schedule schedule = new Schedule(QUESTION_ID, true, HOURLY, REPEAT);

        boolean created = ScheduleStore.saveSchedule(context, schedule);
        assertTrue(created);

        Schedule fromDB = ScheduleStore.getSchedule(context, QUESTION_ID);
        assertNotNull(fromDB);
    }

    @Test
    public void testUpdate() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID, true, HOURLY, REPEAT));
        HourlyRepeat updatedRepeat = new HourlyRepeat(2, LocalTime.of(8, 0), LocalTime.of(21, 0));

        boolean updated = ScheduleStore.saveSchedule(context,
                new Schedule(QUESTION_ID, true, HOURLY, updatedRepeat));
        assertTrue(updated);

        Schedule fromDB = ScheduleStore.getSchedule(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertEquals(updatedRepeat.toDbString(), fromDB.getRepeat().toDbString());
    }

    @Test
    public void testGetOne() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID, false, HOURLY, REPEAT));

        Schedule fromDB = ScheduleStore.getSchedule(context, QUESTION_ID);

        assertNotNull(fromDB);
        assertEquals(QUESTION_ID, fromDB.getQuestionId());
        assertFalse(fromDB.isOn());
        assertEquals("2;08:00;20:00", fromDB.getRepeat().toDbString());
    }

    @Test
    public void testGetAll() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID, false, HOURLY, REPEAT));
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID_2, false, HOURLY, REPEAT));
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID_3, false, HOURLY, REPEAT));

        Collection<Schedule> allSchedules = ScheduleStore.getAllSchedules(context);

        assertEquals(3, allSchedules.size());
    }

    @Test
    public void testSwitchOff() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID, true, HOURLY, REPEAT));

        boolean updated = ScheduleStore.switchOnOff(context, QUESTION_ID, false);
        assertTrue(updated);

        Schedule fromDB = ScheduleStore.getSchedule(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertFalse(fromDB.isOn());
    }

    @Test
    public void testSwitchOn() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.saveSchedule(context, new Schedule(QUESTION_ID, false, HOURLY, REPEAT));

        boolean updated = ScheduleStore.switchOnOff(context, QUESTION_ID, true);
        assertTrue(updated);

        Schedule fromDB = ScheduleStore.getSchedule(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertTrue(fromDB.isOn());
    }

    @Test(expected = RuntimeException.class)
    public void testFailOnSwitchNonExistent() {
        Context context = InstrumentationRegistry.getTargetContext();
        ScheduleStore.switchOnOff(context, QUESTION_ID, true);
    }
}
