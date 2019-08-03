package feelings.guide.ui.question

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import feelings.guide.*
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.question_list.*

class QuestionListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_list_host)

        if (savedInstanceState == null) {
            val fragment = QuestionListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.question_list_content, fragment)
                .commit()
        }
    }

    internal fun navigateToAnswer(questionId: Long) {
        startActivityForResult(Intent(this, AnswerActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, questionId)
        }, ADD_ANSWER_REQUEST_CODE)
    }

    internal fun navigateToAnswerLog(questionId: Long?) {
        startActivity(Intent(this, AnswerLogActivity::class.java).apply {
            if (questionId != null) putExtra(QUESTION_ID_PARAM, questionId)
        })
    }

    internal fun navigateToSettings() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), SETTINGS_REQUEST_CODE)
    }

    fun showPopupMenu(v: View) {
        val questionListFragment = supportFragmentManager.findFragmentById(R.id.question_list_content)
        if (questionListFragment != null)
            (questionListFragment as QuestionListFragment).showPopupMenu(v)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val needToRefresh = data!!.getBooleanExtra(REFRESH_QUESTIONS_RESULT_KEY, false)
            if (needToRefresh) {
                val questionListFragment = supportFragmentManager.findFragmentById(R.id.question_list_content)
                if (questionListFragment != null)
                    (questionListFragment as QuestionListFragment).refreshAll()
            }
        } else if (requestCode == ADD_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Snackbar.make(questions_list_layout, R.string.msg_answer_added_success, Snackbar.LENGTH_LONG).show()
        }
    }
}
