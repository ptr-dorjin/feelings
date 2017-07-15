package feelings.helper.repeat;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalTime;

import java.util.Locale;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.threeten.bp.DayOfWeek.FRIDAY;
import static org.threeten.bp.DayOfWeek.MONDAY;
import static org.threeten.bp.DayOfWeek.TUESDAY;
import static org.threeten.bp.DayOfWeek.WEDNESDAY;
import static org.threeten.bp.LocalTime.of;

@RunWith(AndroidJUnit4.class)
public class RepeatInstrumentedTest {

    private void setLocale(String language, String country) {
        Locale locale = new Locale(language, country);
        // here we update locale for date formatters
        Locale.setDefault(locale);
        // here we update locale for app resources
        Resources res = InstrumentationRegistry.getTargetContext().getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Test
    public void testHourlyHumanReadableStringRussian() {
        setLocale("ru", "RU");
        Context context = InstrumentationRegistry.getTargetContext();
        HourlyRepeat repeat = new HourlyRepeat(2, of(8, 0), of(20, 0));

        String string = repeat.toHumanReadableString(context);

        assertEquals("каждые 2 часа с 08:00 по 20:00", string);
    }

    @Test
    public void testDailyHumanReadableStringRussian() {
        setLocale("ru", "RU");
        Context context = InstrumentationRegistry.getTargetContext();
        DailyRepeat repeat = new DailyRepeat(new TreeSet<>(asList(of(9, 0), of(12, 0), of(15, 0))));

        String string = repeat.toHumanReadableString(context);

        assertEquals("каждый день в 09:00, 12:00, 15:00", string);
    }

    @Test
    public void testWeeklyHumanReadableStringRussian() {
        setLocale("ru", "RU");
        Context context = InstrumentationRegistry.getTargetContext();
        WeeklyRepeat repeat = new WeeklyRepeat(TUESDAY, LocalTime.of(8, 0));

        String string = repeat.toHumanReadableString(context);

        assertEquals("по вт в 08:00", string);
    }

    @Test
    public void testWeeklyCustomHumanReadableStringRussian() {
        setLocale("ru", "RU");
        Context context = InstrumentationRegistry.getTargetContext();
        WeeklyCustomRepeat repeat = new WeeklyCustomRepeat(
                new TreeSet<>(asList(MONDAY, WEDNESDAY, FRIDAY)), of(21, 0));

        String string = repeat.toHumanReadableString(context);

        assertEquals("по пн, ср, пт в 21:00", string);
    }
}
