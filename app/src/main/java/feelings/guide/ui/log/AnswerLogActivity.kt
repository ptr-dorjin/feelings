package feelings.guide.ui.log

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.*
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.answer.AnswerActivity
import feelings.guide.ui.question.QuestionClearLogDialogFragment
import feelings.guide.ui.question.QuestionClearLogDialogListener
import feelings.guide.util.ToastUtil
import kotlinx.android.synthetic.main.answer_log_by_question_activity.*

private const val DEFAULT_QUESTION_ID: Long = -1

class AnswerLogActivity : BaseActivity(),
    ClearLogFullDialogListener,
    ClearLogDeletedDialogListener,
    QuestionClearLogDialogListener,
    AnswerLogSwipeCallback.AnswerLogSwipeListener {

    private var questionId: Long = 0
    private var isFull: Boolean = false
    private lateinit var adapter: AnswerLogAdapter
    private var lastDeleted: Answer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionId = intent.getLongExtra(QUESTION_ID_PARAM, DEFAULT_QUESTION_ID)
        isFull = questionId == DEFAULT_QUESTION_ID

        setContentView(
            when {
                isFull -> R.layout.answer_log_full_activity
                else -> R.layout.answer_log_by_question_activity
            }
        )

        adapter = AnswerLogAdapter(this, isFull, questionId)
        val notEmpty = adapter.isNotEmpty
        answerLogRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        answerLogEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
        fillQuestionText()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        answerLogRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            answerLogRV.context,
            layoutManager.orientation
        )
        answerLogRV.addItemDecoration(dividerItemDecoration)

        answerLogRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(AnswerLogSwipeCallback(this, this))
        itemTouchHelper.attachToRecyclerView(answerLogRV)
    }

    private fun fillQuestionText() {
        if (!isFull) {
            questionTextOnAnswerLog.text = QuestionService.getQuestionText(this, questionId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (adapter.isNotEmpty) {
            menuInflater.inflate(
                if (isFull) R.menu.answer_log_full_menu
                else R.menu.answer_log_by_question_menu,
                menu
            )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.clear_log_full -> {
            showClearLogFullConfirmation()
            true
        }
        R.id.clear_log_deleted -> {
            showClearLogDeletedConfirmation()
            true
        }
        R.id.clear_log_by_question -> {
            showClearLogByQuestionConfirmation()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showClearLogFullConfirmation() {
        ClearLogFullDialogFragment().show(supportFragmentManager, ClearLogFullDialogFragment::class.java.simpleName)
    }

    private fun showClearLogDeletedConfirmation() {
        ClearLogDeletedDialogFragment().show(supportFragmentManager, ClearLogDeletedDialogFragment::class.java.simpleName)
    }

    private fun showClearLogByQuestionConfirmation() {
        QuestionClearLogDialogFragment().show(supportFragmentManager, QuestionClearLogDialogFragment::class.java.simpleName)
    }

    override fun onClearLogFullConfirmed() {
        AnswerStore.deleteAll(this)
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_full_success))
        adapter.refresh()
    }

    override fun onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(this)
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_deleted_success))
        adapter.refresh()
    }

    override fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(this, questionId)
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_by_question_success))
        adapter.refresh()
    }

    override fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(this, lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()
        showUndoDeleteSnackbar()
    }

    private fun showUndoDeleteSnackbar() {
        val snackbar = Snackbar.make(answerLogView, R.string.msg_answer_deleted_success, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.snackbar_undo) { undoDelete() }
        snackbar.show()
    }

    private fun undoDelete() {
        AnswerStore.saveAnswer(this, lastDeleted!!)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()
    }

    override fun onEditAnswer(position: Int) {
        val answer = adapter.getByPosition(position)
        startActivityForResult(Intent(this, AnswerActivity::class.java).apply {
            putExtra(QUESTION_ID_PARAM, answer.questionId)
            putExtra(ANSWER_ID_PARAM, answer.id)
        }, UPDATE_ANSWER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onEditAnswerResult(data)
        }
    }

    private fun onEditAnswerResult(data: Intent?) {
        val needToRefresh = data?.getBooleanExtra(REFRESH_ANSWER_LOG_RESULT_KEY, false) ?: false
        if (needToRefresh) {
            //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
            adapter.refresh()

            //redraw recyclerView
            answerLogRV.adapter = adapter

            //scroll to updated answer
            val answerId = data?.getLongExtra(UPDATED_ANSWER_ID_RESULT_KEY, -1) ?: -1
            if (answerId > 0) {
                val position = adapter.getPositionById(answerId)
                if (position > -1) {
                    answerLogRV.scrollToPosition(position)
                }
            }
        }
    }
}
