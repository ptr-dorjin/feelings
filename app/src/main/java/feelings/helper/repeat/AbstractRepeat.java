package feelings.helper.repeat;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

abstract class AbstractRepeat implements Repeat {

    Clock clock = Clock.systemDefaultZone();

    LocalDateTime todayAt(LocalTime time) {
        return LocalDateTime.of(LocalDate.now(clock), time);
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
