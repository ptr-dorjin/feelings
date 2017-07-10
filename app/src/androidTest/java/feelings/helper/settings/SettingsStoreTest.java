package feelings.helper.settings;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import feelings.helper.repetition.HourlyRepetition;

import static feelings.helper.TestDateTimeUtil.time;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SettingsStoreTest {
    private static final HourlyRepetition REPETITION = new HourlyRepetition(2, time(8, 0), time(20, 0));
    private static final int QUESTION_ID = 1;

    @After
    public void tearDown() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.delete(context, QUESTION_ID);
        SettingsStore.delete(context, 2);
        SettingsStore.delete(context, 3);
    }

    @Test
    public void testCreate() {
        Context context = InstrumentationRegistry.getTargetContext();
        Settings settings = new Settings(QUESTION_ID, true, REPETITION);

        boolean created = SettingsStore.saveSettings(context, settings);
        assertTrue(created);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
    }

    @Test
    public void testUpdate() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, true, REPETITION));
        HourlyRepetition updatedRepetition = new HourlyRepetition(2, time(8, 0), time(21, 0));

        boolean updated = SettingsStore.saveSettings(context,
                new Settings(QUESTION_ID, true, updatedRepetition));
        assertTrue(updated);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertEquals(updatedRepetition.toString(), fromDB.getRepetition().toString());
    }

    @Test
    public void testGetOne() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPETITION));

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);

        assertNotNull(fromDB);
        assertEquals(QUESTION_ID, fromDB.getQuestionId());
        assertFalse(fromDB.isOn());
        assertEquals("2;08:00;20:00", fromDB.getRepetition().toString());
    }

    @Test
    public void testGetAll() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPETITION));
        SettingsStore.saveSettings(context, new Settings(2, false, REPETITION));
        SettingsStore.saveSettings(context, new Settings(3, false, REPETITION));

        Collection<Settings> allSettings = SettingsStore.getAllSettings(context);

        assertEquals(3, allSettings.size());
    }

    @Test
    public void testSwitchOff() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, true, REPETITION));

        boolean updated = SettingsStore.switchOnOff(context, QUESTION_ID, false);
        assertTrue(updated);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertFalse(fromDB.isOn());
    }

    @Test
    public void testSwitchOn() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPETITION));

        boolean updated = SettingsStore.switchOnOff(context, QUESTION_ID, true);
        assertTrue(updated);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertTrue(fromDB.isOn());
    }

    @Test(expected = RuntimeException.class)
    public void testFailOnSwitchNonExistent() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.switchOnOff(context, QUESTION_ID, true);
    }
}
