package feelings.guide.ui.log

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import feelings.guide.ANSWER_ID_PARAM
import feelings.guide.ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY
import feelings.guide.EDIT_ANSWER_REQUEST_CODE
import feelings.guide.EXPORT_LOG_REQUEST_CODE
import feelings.guide.QUESTION_ID_PARAM
import feelings.guide.R
import feelings.guide.UPDATED_ANSWER_ID_RESULT_KEY
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.export.exportLog
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.log.byquestion.AnswerLogByQuestionFragment
import feelings.guide.ui.log.full.AnswerLogFullFragment
import feelings.guide.util.EXPORT_FILE_NAME_FORMATTER
import kotlinx.android.synthetic.main.answer_log_host.*
import org.threeten.bp.LocalDateTime

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
                isFull -> AnswerLogFullFragment(this::createFileForExport)
                else -> AnswerLogByQuestionFragment(questionId, this::createFileForExport)
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
        if (requestCode == EDIT_ANSWER_REQUEST_CODE && resultCode == RESULT_OK) {
            // returned from editing the answer
            val answerLogFragment = supportFragmentManager.findFragmentById(R.id.answerLogContent)
            val answerId = data?.getLongExtra(UPDATED_ANSWER_ID_RESULT_KEY, -1) ?: -1
            val answerIsUpdated = data?.getBooleanExtra(ANSWER_IS_ADDED_OR_UPDATED_RESULT_KEY, false)
                    ?: false
            if (isFull)
                (answerLogFragment as? AnswerLogFullFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
            else
                (answerLogFragment as? AnswerLogByQuestionFragment)?.onReturnFromEditAnswer(answerId, answerIsUpdated)
        } else if (requestCode == EXPORT_LOG_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (data?.data == null) {
                    Snackbar.make(answerLogContent, "Choose file location to export.",
                            Snackbar.LENGTH_LONG).show()
                }
                data?.data?.let { uri: Uri ->
                    val answers = AnswerStore.getAnswersForExport(this, questionId)
                    contentResolver.openOutputStream(uri).use {
                        if (it == null) {
                            Snackbar.make(answerLogContent, "Cannot write to file $uri",
                                    Snackbar.LENGTH_LONG).show()
                            return
                        }

                        exportLog(answers, it)

                        Snackbar.make(answerLogContent, "Successfully exported log",
                                Snackbar.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Snackbar.make(answerLogContent, "An error occurred while exporting the file. ${e.message}",
                        Snackbar.LENGTH_LONG).show()
                Log.e("export", "An error occurred while creating a file.", e)
            }
        }
    }

    private fun createFileForExport() {
        try {
            val id = if (isFull) "full" else questionId.toString()
            val timestamp = LocalDateTime.now().format(EXPORT_FILE_NAME_FORMATTER)
            val fileName = "feelings-guide-log-$id-$timestamp.csv"

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, fileName)
            }
            startActivityForResult(intent, EXPORT_LOG_REQUEST_CODE)
        } catch (e: Exception) {
            Snackbar.make(answerLogContent, "An error occurred while creating a file. ${e.message}",
                    Snackbar.LENGTH_LONG).show()
            Log.e("export", "An error occurred while creating a file.", e)
        }
    }
}
