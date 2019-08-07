package feelings.guide.ui.question

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import feelings.guide.*
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.settings.SettingsActivity

class QuestionListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_list_host)

        if (savedInstanceState == null) {
            val fragment = QuestionListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.questionListContent, fragment)
                .commit()
        }
    }

    internal fun navigateToAnswer(questionId: Long) {
        startActivityForResult(Intent(this, AnswerActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, questionId)
        }, ADD_ANSWER_REQUEST_CODE)
    }

    internal fun navigateToAnswerLogFull() {
        startActivity(Intent(this, AnswerLogActivity::class.java))
    }

    internal fun navigateToAnswerLogByQuestion(questionId: Long) {
        startActivity(Intent(this, AnswerLogActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, questionId)
        })
    }

    internal fun navigateToSettings() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), SETTINGS_REQUEST_CODE)
    }

    fun showPopupMenu(v: View) {
        val questionListFragment = supportFragmentManager.findFragmentById(R.id.questionListContent)
        if (questionListFragment != null)
            (questionListFragment as QuestionListFragment).showPopupMenu(v)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val questionListFragment = supportFragmentManager.findFragmentById(R.id.questionListContent)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val needToRefresh = data?.getBooleanExtra(REFRESH_QUESTIONS_RESULT_KEY, false) ?: false
            (questionListFragment as? QuestionListFragment)?.onReturnFromSettings(needToRefresh)
        } else if (requestCode == ADD_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // returned from adding the answer
            val answerIsAdded = data?.getBooleanExtra(ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY, false) ?: false
            (questionListFragment as? QuestionListFragment)?.onReturnFromAddAnswer(answerIsAdded)
        }
    }
}
