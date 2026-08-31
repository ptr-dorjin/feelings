package feelings.guide.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { DARK, LIGHT, SYSTEM }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dateFormatPattern: String = DEFAULT_DATE_FORMAT,
    val timeFormatPattern: String = DEFAULT_TIME_FORMAT,
)

const val DEFAULT_DATE_FORMAT = "d MMM yyyy"
const val DEFAULT_TIME_FORMAT = "HH:mm"

val DATE_FORMAT_OPTIONS = listOf("d MMM yyyy", "MMM d yyyy", "dd.MM.yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
val TIME_FORMAT_OPTIONS = listOf("HH:mm", "hh:mm a", "h:mm a")

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
private val DATE_FORMAT_KEY = stringPreferencesKey("date_format")
private val TIME_FORMAT_KEY = stringPreferencesKey("time_format")

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[THEME_MODE_KEY]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            dateFormatPattern = prefs[DATE_FORMAT_KEY] ?: DEFAULT_DATE_FORMAT,
            timeFormatPattern = prefs[TIME_FORMAT_KEY] ?: DEFAULT_TIME_FORMAT,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE_KEY] = mode.name }
    }

    suspend fun setDateFormatPattern(pattern: String) {
        dataStore.edit { it[DATE_FORMAT_KEY] = pattern }
    }

    suspend fun setTimeFormatPattern(pattern: String) {
        dataStore.edit { it[TIME_FORMAT_KEY] = pattern }
    }
}
