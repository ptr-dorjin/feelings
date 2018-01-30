package feelings.guide.util;

import org.threeten.bp.format.DateTimeFormatter;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static final DateTimeFormatter DB_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String ANSWER_LOG_FORMAT = "d MMM yyyy, HH:mm";
}
