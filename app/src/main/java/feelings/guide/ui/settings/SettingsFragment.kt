package feelings.guide.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import feelings.guide.R
import feelings.guide.REFRESH_QUESTIONS_RESULT_KEY
import feelings.guide.question.QuestionService
import feelings.guide.util.ToastUtil

const val DATE_FORMAT_KEY = "Feelings.Guide.Date.Format"
const val TIME_FORMAT_KEY = "Feelings.Guide.Time.Format"
private const val RESTORE_BUILT_IN_QUESTIONS_KEY = "Feelings.Guide.Restore.Built.In.Questions"

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpDateFormatPreference()
        setUpTimeFormatPreference()
        setUpRestoreBuiltInQuestionsPreference()
    }

    private fun setUpDateFormatPreference() {
        val dateFormatPreference: Preference? = preferenceScreen.findPreference(DATE_FORMAT_KEY)
        dateFormatPreference?.setOnPreferenceChangeListener { _, _ ->
            activity?.let { ToastUtil.showShort(it, getString(R.string.msg_date_format_success)) }
            true
        }
    }

    private fun setUpTimeFormatPreference() {
        val dateFormatPreference: Preference? = preferenceScreen.findPreference(TIME_FORMAT_KEY)
        dateFormatPreference?.setOnPreferenceChangeListener { _, _ ->
            activity?.let { ToastUtil.showShort(it, getString(R.string.msg_time_format_success)) }
            true
        }
    }

    private fun setUpRestoreBuiltInQuestionsPreference() {
        val clearLogPreference: Preference? = preferenceScreen.findPreference(RESTORE_BUILT_IN_QUESTIONS_KEY)
        clearLogPreference?.setOnPreferenceClickListener {
            activity?.let {
                AlertDialog.Builder(it)
                    .setMessage(R.string.title_confirm_restore_built_in_questions_dialog)
                    .setPositiveButton(R.string.btn_restore) { _, _ ->
                        QuestionService.restoreHidden(it)
                        ToastUtil.showShort(it, getString(R.string.msg_restore_built_in_questions_success))
                        val data = Intent()
                        data.putExtra(REFRESH_QUESTIONS_RESULT_KEY, true)
                        it.setResult(RESULT_OK, data)
                    }
                    .setNegativeButton(R.string.btn_cancel) { dialog, _ -> dialog.dismiss() }.create()
                    .show()
            }
            true
        }
    }
}
