package feelings.guide.profile

import android.content.Context
import android.content.SharedPreferences

import feelings.guide.R
import feelings.guide.question.QuestionService
import feelings.guide.util.ToastUtil

class LocaleChangeListener(private var context: Context?) : SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (LocaleUtil.SELECTED_LANGUAGE == key) {
            context = LocaleUtil.setLocale(context!!)
            QuestionService.changeLanguage(context!!)
            ToastUtil.showLong(context!!, context!!.getString(R.string.msg_change_language_restart))
        }
    }
}
