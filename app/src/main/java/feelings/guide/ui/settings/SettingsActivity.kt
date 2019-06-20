package feelings.guide.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import feelings.guide.R
import feelings.guide.profile.LocaleUtil
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity
import feelings.guide.util.ToastUtil

class SettingsActivity : BaseActivity() {

    //keep the listener link to not get garbage collected from pref's WeakHashMap
    private val localeChangeListener: LocaleChangeListener = LocaleChangeListener(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Display the fragment as the main content.
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(localeChangeListener)
    }
}

/**
 * This listener should be in the activity, not in the fragment
 */
private class LocaleChangeListener(private var context: Context) : SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (LocaleUtil.SELECTED_LANGUAGE == key) {
            context = LocaleUtil.setLocale(context)
            QuestionService.changeLanguage(context)
            ToastUtil.showLong(context, context.getString(R.string.msg_change_language_restart))
        }
    }
}

