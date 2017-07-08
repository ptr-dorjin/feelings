package feelings.helper;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalTime;

import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.junit.Assert.assertEquals;

public class TestDateTimeUtil {

    public static void mockTime(int hour, int minute) {
        setCurrentMillisFixed(new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .getMillis());
    }

    public static void mockDayOfWeek(int dayOfWeek) {
        setCurrentMillisFixed(new DateTime()
                .withDayOfWeek(dayOfWeek)
                .getMillis());
    }

    public static void mockDayOfWeekAndTime(int dayOfWeek, int hour, int minute) {
        setCurrentMillisFixed(new DateTime()
                .withDayOfWeek(dayOfWeek)
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .getMillis());
    }

    public static LocalTime time(int hour, int minute) {
        return new LocalTime(hour, minute);
    }

    public static void assertTime(int hour, int minute, DateTime dateTime) {
        Assert.assertEquals(hour, dateTime.getHourOfDay());
        Assert.assertEquals(minute, dateTime.getMinuteOfHour());
    }

    public static void assertToday(DateTime next) {
        assertEquals(DateTime.now().getDayOfYear(), next.getDayOfYear());
    }

    public static void assertTomorrow(DateTime next) {
        assertEquals(1, Days.daysBetween(DateTime.now().toLocalDate(), next.toLocalDate()).getDays());
    }

    public static void assertDays(DateTime next, int diff) {
        assertEquals(diff, Days.daysBetween(DateTime.now().toLocalDate(), next.toLocalDate()).getDays());
    }

    public static void assertDayOfWeek(DateTime next, int dayOfWeek) {
        assertEquals(dayOfWeek, next.getDayOfWeek());
    }
}
