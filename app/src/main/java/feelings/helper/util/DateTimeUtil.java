package feelings.helper.util;

import org.threeten.bp.format.DateTimeFormatter;

import feelings.helper.profile.Profile;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

public class DateTimeUtil {

    public static final DateTimeFormatter ANSWER_LOG_FORMATTER = ofPattern("d MMM yyyy HH:mm", Profile.getLocale());
    public static final DateTimeFormatter DB_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
}
