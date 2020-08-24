package feelings.guide

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import feelings.guide.profile.LocaleUtil

const val QUESTION_ID_PARAM = "question_id"
const val ANSWER_ID_PARAM = "answer_id"
const val SETTINGS_REQUEST_CODE = 777
const val ADD_ANSWER_REQUEST_CODE = 102
const val EDIT_ANSWER_REQUEST_CODE = 103
const val EXPORT_LOG_REQUEST_CODE = 104

//const val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 500
const val REFRESH_QUESTIONS_RESULT_KEY = "should-refresh-questions"
const val UPDATED_ANSWER_ID_RESULT_KEY = "updated-answer-id"
const val ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY = "answer-is-added-or-updated"

@Suppress("unused")
class FeelingsApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtil.setLocale(this)
    }
}

