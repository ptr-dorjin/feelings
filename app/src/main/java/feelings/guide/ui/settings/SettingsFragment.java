package feelings.guide.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import feelings.guide.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
