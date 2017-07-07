package feelings.helper.repetition;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Base class for all types of repetitions
 */

public interface Repetition {

    DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

    /**
     * @return Type of this repetition.
     */
    String getType();

    /**
     * @return Next DateTime when alarm should trigger.
     */
    DateTime getNextTime();
}
