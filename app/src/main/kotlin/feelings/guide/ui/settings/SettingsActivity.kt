package feelings.guide.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.databinding.SettingsHostBinding
import feelings.guide.profile.LocaleUtil
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity

//keep the listener link to not get garbage collected from pref's WeakHashMap
@SuppressLint("StaticFieldLeak")
private val localeChangeListener = LocaleChangeListener()

class SettingsActivity : BaseActivity() {
    private lateinit var binding: SettingsHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display the fragment as the main content.
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsContent, SettingsFragment())
                .commit()

        localeChangeListener.context = this
        localeChangeListener.view = binding.settingsContent
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(localeChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(localeChangeListener)
    }
}

/**
 * This listener should be in the activity, not in the fragment
 */
private class LocaleChangeListener(
        var context: Context? = null,
        var view: View? = null
) : SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (SELECTED_LANGUAGE_KEY == key && context != null) {
            context = LocaleUtil.setLocale(context!!)
            QuestionService.changeLanguage(context!!)
            view?.let {
                Snackbar.make(it, R.string.msg_change_language_restart, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }
}

