package feelings.guide.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import feelings.guide.profile.LocaleChangeListener;
import feelings.guide.ui.BaseActivity;

public class SettingsActivity extends BaseActivity {

    //keep the listener link to not get garbage collected from pref's WeakHashMap
    @SuppressWarnings("FieldCanBeLocal")
    private LocaleChangeListener localeChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        setUpLocaleChangeListener();
    }

    private void setUpLocaleChangeListener() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        localeChangeListener = new LocaleChangeListener(this);
        prefs.registerOnSharedPreferenceChangeListener(localeChangeListener);
    }
}
