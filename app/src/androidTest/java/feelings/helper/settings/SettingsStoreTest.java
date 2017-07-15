package feelings.helper.settings;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalTime;

import java.util.Collection;

import feelings.helper.repeat.HourlyRepeat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SettingsStoreTest {
    private static final HourlyRepeat REPEAT = new HourlyRepeat(2, LocalTime.of(8, 0), LocalTime.of(20, 0));
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
        Settings settings = new Settings(QUESTION_ID, true, REPEAT);

        boolean created = SettingsStore.saveSettings(context, settings);
        assertTrue(created);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
    }

    @Test
    public void testUpdate() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, true, REPEAT));
        HourlyRepeat updatedRepeat = new HourlyRepeat(2, LocalTime.of(8, 0), LocalTime.of(21, 0));

        boolean updated = SettingsStore.saveSettings(context,
                new Settings(QUESTION_ID, true, updatedRepeat));
        assertTrue(updated);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertEquals(updatedRepeat.toString(), fromDB.getRepeat().toString());
    }

    @Test
    public void testGetOne() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPEAT));

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);

        assertNotNull(fromDB);
        assertEquals(QUESTION_ID, fromDB.getQuestionId());
        assertFalse(fromDB.isOn());
        assertEquals("2;08:00;20:00", fromDB.getRepeat().toString());
    }

    @Test
    public void testGetAll() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPEAT));
        SettingsStore.saveSettings(context, new Settings(2, false, REPEAT));
        SettingsStore.saveSettings(context, new Settings(3, false, REPEAT));

        Collection<Settings> allSettings = SettingsStore.getAllSettings(context);

        assertEquals(3, allSettings.size());
    }

    @Test
    public void testSwitchOff() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, true, REPEAT));

        boolean updated = SettingsStore.switchOnOff(context, QUESTION_ID, false);
        assertTrue(updated);

        Settings fromDB = SettingsStore.getSettings(context, QUESTION_ID);
        assertNotNull(fromDB);
        assertFalse(fromDB.isOn());
    }

    @Test
    public void testSwitchOn() {
        Context context = InstrumentationRegistry.getTargetContext();
        SettingsStore.saveSettings(context, new Settings(QUESTION_ID, false, REPEAT));

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
