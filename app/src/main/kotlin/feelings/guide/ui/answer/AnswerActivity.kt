package feelings.guide.ui.answer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import feelings.guide.*
import feelings.guide.ui.BaseActivity

class AnswerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_host)

        if (savedInstanceState == null) {
            val questionId = intent.getLongExtra(QUESTION_ID_PARAM, -1)
            val answerId = intent.getLongExtra(ANSWER_ID_PARAM, -1)

            val fragment = AnswerFragment(questionId, answerId)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.answerContent, fragment)
                .commit()
        }
    }

    internal fun navigateBack(updated: Boolean, answerId: Long? = -1): Boolean {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(UPDATED_ANSWER_ID_RESULT_KEY, answerId)
            putExtra(ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY, updated)
        })
        finish()
        return true
    }

    override fun onBackPressed() {
        val answerFragment = supportFragmentManager.findFragmentById(R.id.answerContent)
        val answerId = (answerFragment as? AnswerFragment)?.answerId
        navigateBack(false, answerId)
    }
}
