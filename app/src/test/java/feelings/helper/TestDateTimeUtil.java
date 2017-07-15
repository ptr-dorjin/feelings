package feelings.helper;

import org.threeten.bp.Clock;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import feelings.helper.repeat.Repeat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.threeten.bp.LocalDateTime.now;

public class TestDateTimeUtil {

    public static Clock mockTime(int hour, int minute, Repeat repeat) {
        return mock(repeat, LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute)
        );
    }

    public static Clock mockDayOfWeek(DayOfWeek dayOfWeek, Repeat repeat) {
        return mock(repeat, LocalDateTime.now()
                .with(dayOfWeek)
        );
    }

    public static Clock mockDayOfWeekAndTime(DayOfWeek dayOfWeek, int hour, int minute, Repeat repeat) {
        return mock(repeat, LocalDateTime.now()
                .with(dayOfWeek)
                .withHour(hour)
                .withMinute(minute)
        );
    }

    private static Clock mock(Repeat repeat, LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant fixedInstant = localDateTime.atZone(zoneId).toInstant();
        Clock fixed = Clock.fixed(fixedInstant, zoneId);
        repeat.setClock(fixed);
        return fixed;
    }

    public static void assertFuture(LocalDateTime nextTime, Clock clock) {
        assertTrue(nextTime.isAfter(now(clock)));
    }

    public static void assertTime(int hour, int minute, LocalDateTime dateTime) {
        assertEquals(hour, dateTime.getHour());
        assertEquals(minute, dateTime.getMinute());
    }

    public static void assertToday(LocalDateTime next, Clock clock) {
        assertEquals(LocalDateTime.now(clock).getDayOfYear(), next.getDayOfYear());
    }

    public static void assertTomorrow(LocalDateTime next, Clock clock) {
        assertEquals(LocalDate.now(clock).plusDays(1), next.toLocalDate());
    }

    public static void assertDays(LocalDateTime next, int diff, Clock clock) {
        assertEquals(LocalDate.now(clock).plusDays(diff), next.toLocalDate());
    }

    public static void assertDayOfWeek(LocalDateTime next, DayOfWeek dayOfWeek) {
        assertEquals(dayOfWeek, next.getDayOfWeek());
    }
}
