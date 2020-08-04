package feelings.guide.ui.log

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import feelings.guide.ANSWER_ID_PARAM
import feelings.guide.ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY
import feelings.guide.EDIT_ANSWER_REQUEST_CODE
import feelings.guide.QUESTION_ID_PARAM
import feelings.guide.R
import feelings.guide.UPDATED_ANSWER_ID_RESULT_KEY
import feelings.guide.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.log.byquestion.AnswerLogByQuestionFragment
import feelings.guide.ui.log.full.AnswerLogFullFragment
import kotlinx.android.synthetic.main.answer_log_host.*

const val DEFAULT_QUESTION_ID: Long = -1

class AnswerLogActivity : BaseActivity() {
    private var isFull: Boolean = false
    private var questionId: Long = DEFAULT_QUESTION_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_log_host)

        if (savedInstanceState == null) {
            questionId = intent.getLongExtra(QUESTION_ID_PARAM, DEFAULT_QUESTION_ID)

            isFull = questionId == DEFAULT_QUESTION_ID
            val fragment = when {
                isFull -> AnswerLogFullFragment(this::doExport)
                else -> AnswerLogByQuestionFragment(questionId, this::doExport)
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
            val answerIsUpdated = data?.getBooleanExtra(ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY, false)
                    ?: false
            if (isFull)
                (answerLogFragment as? AnswerLogFullFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
            else
                (answerLogFragment as? AnswerLogByQuestionFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                when (grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> doExport()
                    PackageManager.PERMISSION_DENIED -> Snackbar.make(answerLogContent,
                            "Cannot export without write storage permission",
                            Snackbar.LENGTH_LONG)
                            .show()
                }
            }
        }
    }

    private fun doExport() {
        val answers = AnswerStore.getAnswersForExport(this, questionId)
        Snackbar.make(answerLogContent, "Exporting ${answers.size} answers", Snackbar.LENGTH_LONG)
                .show()
    }
}
