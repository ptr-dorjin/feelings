package feelings.guide.profile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocaleUtil {
    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList(
            Locale.ENGLISH.getLanguage(),
            new Locale("ru").getLanguage());

    static final String SELECTED_LANGUAGE = "Feelings.Guide.Selected.Language";

    public static Context setLocale(Context context) {
        String defaultLanguage = Locale.getDefault().getLanguage();
        if (!SUPPORTED_LANGUAGES.contains(defaultLanguage)) {
            defaultLanguage = Locale.ENGLISH.getLanguage();
        }

        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    private static Context setLocale(Context context, String language) {
        persist(context, language);

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            ? updateResources(context, language)
            : updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
