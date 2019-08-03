package feelings.guide.ui.log

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import feelings.guide.ANSWER_ID_PARAM
import feelings.guide.QUESTION_ID_PARAM
import feelings.guide.R
import feelings.guide.UPDATE_ANSWER_REQUEST_CODE
import feelings.guide.answer.Answer
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import kotlinx.android.synthetic.main.answer_log_by_question_activity.*

class AnswerLogActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_log_host)

        if (savedInstanceState == null) {
            val fragment = AnswerLogFragment(this::navigateToEditAnswer)
            fragment.arguments = intent.extras
            supportFragmentManager
                .beginTransaction()
                .add(R.id.answer_log_content, fragment)
                .commit()
        }
    }

    private fun navigateToEditAnswer(answer: Answer) {
        startActivityForResult(Intent(this, AnswerActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, answer.questionId)
            putExtra(ANSWER_ID_PARAM, answer.id)
        }, UPDATE_ANSWER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // returned from editing the answer
            val questionListFragment = supportFragmentManager.findFragmentById(R.id.answer_log_content)
            if (questionListFragment != null)
                (questionListFragment as AnswerLogFragment).onReturnFromEditAnswer(data)
            Snackbar.make(answerLogActivityLayout, R.string.msg_answer_updated_success, Snackbar.LENGTH_LONG).show()
        }
    }
}
