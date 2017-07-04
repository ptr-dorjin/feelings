package feelings.helper.repetition;

import org.joda.time.DateTime;

/**
 * Base class for all types of repetitions
 */

public interface Repetition {

    /**
     * @return Next DateTime when alarm should trigger.
     */
    DateTime getNextTime();
}
