package feelings.helper.repeat;

import android.content.Context;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Base class for all types of repeats
 */

public interface Repeat {

    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");
    DateTimeFormatter MINUTE_FORMATTER = DateTimeFormatter.ofPattern("mm");

    /**
     * @return Next DateTime when alarm should trigger.
     */
    LocalDateTime getNextTime();

    /**
     * @return String representation to store in the DB.
     */
    String toDbString();

    /**
     * @return String representation to show to a user.
     */
    String toHumanReadableString(Context context);

    /**
     * The only purpose is to mock in unit tests.
     */
    void setClock(Clock clock);
}
