package feelings.guide.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.profile.LocaleUtil
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : BaseActivity() {

    //keep the listener link to not get garbage collected from pref's WeakHashMap
    private lateinit var localeChangeListener: LocaleChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Display the fragment as the main content.
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsActivityLayout, SettingsFragment())
            .commit()

        localeChangeListener = LocaleChangeListener(this, settingsActivityLayout)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(localeChangeListener)
    }
}

/**
 * This listener should be in the activity, not in the fragment
 */
private class LocaleChangeListener(private var context: Context, private val view: View) :
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (LocaleUtil.SELECTED_LANGUAGE == key) {
            context = LocaleUtil.setLocale(context)
            QuestionService.changeLanguage(context)
            Snackbar.make(view, R.string.msg_change_language_restart, Snackbar.LENGTH_LONG).show()
        }
    }
}

