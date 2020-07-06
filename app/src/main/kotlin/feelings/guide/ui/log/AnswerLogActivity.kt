package feelings.guide.ui.log

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import feelings.guide.*
import feelings.guide.data.answer.Answer
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.log.byquestion.AnswerLogByQuestionFragment
import feelings.guide.ui.log.full.AnswerLogFullFragment

const val DEFAULT_QUESTION_ID: Long = -1

class AnswerLogActivity : BaseActivity() {
    private var isFull: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_log_host)

        if (savedInstanceState == null) {
            val questionId = intent.getLongExtra(QUESTION_ID_PARAM, DEFAULT_QUESTION_ID)

            isFull = questionId == DEFAULT_QUESTION_ID
            val fragment = when {
                isFull -> AnswerLogFullFragment()
                else -> AnswerLogByQuestionFragment(questionId)
            }
            supportFragmentManager
                .beginTransaction()
                .add(R.id.answerLogContent, fragment)
                .commit()
        }
    }

    internal fun navigateToEditAnswer(answer: Answer) {
        startActivityForResult(Intent(this, AnswerActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, answer.questionId)
            putExtra(ANSWER_ID_PARAM, answer.id)
        }, EDIT_ANSWER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // returned from editing the answer
            val answerLogFragment = supportFragmentManager.findFragmentById(R.id.answerLogContent)
            val answerId = data?.getLongExtra(UPDATED_ANSWER_ID_RESULT_KEY, -1) ?: -1
            val answerIsUpdated = data?.getBooleanExtra(ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY, false) ?: false
            if (isFull)
                (answerLogFragment as? AnswerLogFullFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
            else
                (answerLogFragment as? AnswerLogByQuestionFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
        }
    }
}
