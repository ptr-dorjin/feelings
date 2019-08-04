package feelings.guide.ui.log

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.question.QuestionService
import feelings.guide.ui.question.QuestionClearLogDialogFragment
import kotlinx.android.synthetic.main.answer_log_by_question.*


class AnswerLogFragment(
    private var questionId: Long = 0,
    private val onEditAnswerActivityCallback: (Answer) -> Unit
) : Fragment() {

    private var isFull: Boolean = false
    private lateinit var adapter: AnswerLogAdapter
    private var lastDeleted: Answer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isFull = questionId == DEFAULT_QUESTION_ID
        val layout = when {
            isFull -> R.layout.answer_log_full
            else -> R.layout.answer_log_by_question
        }
        return inflater.inflate(layout, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        adapter = AnswerLogAdapter(requireContext(), isFull, questionId)
        val notEmpty = adapter.isNotEmpty
        answerLogRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        answerLogEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
        fillQuestionText()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        answerLogRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            answerLogRV.context,
            layoutManager.orientation
        )
        answerLogRV.addItemDecoration(dividerItemDecoration)

        answerLogRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(AnswerLogSwipeCallback(requireContext(), this::onDeleteAnswer, this::onEditAnswer))
        itemTouchHelper.attachToRecyclerView(answerLogRV)
    }

    private fun fillQuestionText() {
        if (!isFull) {
            questionTextInLogByQuestion.text = QuestionService.getQuestionText(requireContext(), questionId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (adapter.isNotEmpty) {
            inflater.inflate(
                if (isFull) R.menu.answer_log_full_menu
                else R.menu.answer_log_by_question_menu,
                menu
            )
        }
        super.onCreateOptionsMenu(menu, inflater)
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
        ClearLogFullDialogFragment(this::onClearLogFullConfirmed).show(
            requireActivity().supportFragmentManager,
            ClearLogFullDialogFragment::class.java.simpleName
        )
    }

    private fun showClearLogDeletedConfirmation() {
        ClearLogDeletedDialogFragment(this::onClearLogDeletedConfirmed).show(
            requireActivity().supportFragmentManager,
            ClearLogDeletedDialogFragment::class.java.simpleName
        )
    }

    private fun showClearLogByQuestionConfirmation() {
        QuestionClearLogDialogFragment(this::onClearLogByQuestionConfirmed).show(
            requireActivity().supportFragmentManager,
            QuestionClearLogDialogFragment::class.java.simpleName
        )
    }

    private fun onClearLogFullConfirmed() {
        AnswerStore.deleteAll(requireContext())
        Snackbar.make(answerLogLayout, R.string.msg_clear_log_full_success, Snackbar.LENGTH_LONG).show()
        adapter.refresh()
    }

    private fun onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(requireContext())
        Snackbar.make(answerLogLayout, R.string.msg_clear_log_deleted_success, Snackbar.LENGTH_LONG).show()
        adapter.refresh()
    }

    private fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(requireContext(), questionId)
        Snackbar.make(answerLogLayout, R.string.msg_clear_log_by_question_success, Snackbar.LENGTH_LONG).show()
        adapter.refresh()
    }

    private fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(requireContext(), lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        Snackbar.make(answerLogLayout, R.string.msg_answer_deleted_success, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_undo) { undoDelete() }
            .show()
    }

    private fun undoDelete() {
        AnswerStore.saveAnswer(requireContext(), lastDeleted!!)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()
    }

    private fun onEditAnswer(position: Int) {
        val answer = adapter.getByPosition(position)
        onEditAnswerActivityCallback(answer)
    }

    internal fun onReturnFromEditAnswer(answerId: Long, answerIsUpdated: Boolean) {
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        //redraw recyclerView
        answerLogRV.adapter = adapter

        //scroll to the answer
        if (answerId > 0) {
            val position = adapter.getPositionById(answerId)
            if (position > -1) {
                answerLogRV.scrollToPosition(position)
            }
        }
        if (answerIsUpdated)
            Snackbar.make(answerLogLayout, R.string.msg_answer_updated_success, Snackbar.LENGTH_LONG).show()
    }
}