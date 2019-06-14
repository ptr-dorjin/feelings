package feelings.guide.util

import android.content.Context
import android.preference.PreferenceManager
import feelings.guide.ui.settings.SettingsFragment.DATE_FORMAT_KEY
import feelings.guide.ui.settings.SettingsFragment.TIME_FORMAT_KEY
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.ofPattern

private const val ANSWER_LOG_DATE_DEFAULT_FORMAT = "d MMM yyyy"
private const val ANSWER_LOG_TIME_DEFAULT_FORMAT = "HH:mm"

val DB_FORMATTER: DateTimeFormatter = ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

object DateTimeUtil {
    fun getDateTimeFormat(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val dateFormat = preferences.getString(DATE_FORMAT_KEY, ANSWER_LOG_DATE_DEFAULT_FORMAT)
        val timeFormat = preferences.getString(TIME_FORMAT_KEY, ANSWER_LOG_TIME_DEFAULT_FORMAT)
        return "$dateFormat, $timeFormat"
    }
}
