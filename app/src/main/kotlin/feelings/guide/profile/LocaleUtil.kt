package feelings.guide.profile

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.*

object LocaleUtil {
    private val SUPPORTED_LANGUAGES = Arrays.asList(
        Locale.ENGLISH.language,
        Locale("ru").language
    )

    internal const val SELECTED_LANGUAGE = "Feelings.Guide.Selected.Language"

    fun setLocale(context: Context): Context {
        var defaultLanguage = Locale.getDefault().language
        if (!SUPPORTED_LANGUAGES.contains(defaultLanguage)) {
            defaultLanguage = Locale.ENGLISH.language
        }

        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    private fun setLocale(context: Context, language: String?): Context {
        persist(context, language)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            updateResources(context, language)
        else
            updateResourcesLegacy(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SELECTED_LANGUAGE, language)
            .apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}
