package feelings.guide.ui.log.byquestion

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.question.QuestionService
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.log.AnswerLogSwipeCallback
import feelings.guide.ui.log.ExportPermissionDialogFragment
import feelings.guide.ui.question.QuestionClearLogDialogFragment
import kotlinx.android.synthetic.main.answer_log_by_question.*


class AnswerLogByQuestionFragment(
        private var questionId: Long,
        private val exportFn: () -> Unit)
    : Fragment() {

    private lateinit var adapter: AnswerLogByQuestionAdapter
    private var lastDeleted: Answer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.answer_log_by_question, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        adapter = AnswerLogByQuestionAdapter(requireContext(), questionId)
        val notEmpty = adapter.isNotEmpty
        logByQuestionRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        logByQuestionEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
        fillQuestionText()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        logByQuestionRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
                logByQuestionRV.context,
                layoutManager.orientation
        )
        logByQuestionRV.addItemDecoration(dividerItemDecoration)

        logByQuestionRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(
                AnswerLogSwipeCallback(
                        requireContext(),
                        this::onDeleteAnswer,
                        this::onEditAnswer
                )
        )
        itemTouchHelper.attachToRecyclerView(logByQuestionRV)
    }

    private fun fillQuestionText() {
        logByQuestionText.text = QuestionService.getQuestionText(requireContext(), questionId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (adapter.isNotEmpty) {
            inflater.inflate(R.menu.answer_log_by_question_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.clear_log_by_question -> {
            showClearLogByQuestionConfirmation()
            true
        }
        R.id.export_log -> {
            exportLog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showClearLogByQuestionConfirmation() {
        QuestionClearLogDialogFragment(this::onClearLogByQuestionConfirmed)
                .show(requireActivity().supportFragmentManager,
                        QuestionClearLogDialogFragment::class.java.simpleName)
    }

    private fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(requireContext(), questionId)
        Snackbar.make(logByQuestionLayout, R.string.msg_clear_log_by_question_success, Snackbar.LENGTH_LONG).show()
        adapter.refresh()
    }

    private fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(requireContext(), lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        Snackbar.make(logByQuestionLayout, R.string.msg_answer_deleted_success, Snackbar.LENGTH_LONG)
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
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            (requireActivity() as AnswerLogActivity).navigateToEditAnswer(answer)
        }
    }

    internal fun onReturnFromEditAnswer(answerId: Long, answerIsUpdated: Boolean) {
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        //redraw recyclerView
        logByQuestionRV.adapter = adapter

        //scroll to the answer
        if (answerId > 0) {
            val position = adapter.getPositionById(answerId)
            if (position > -1) {
                logByQuestionRV.scrollToPosition(position)
            }
        }
        if (answerIsUpdated)
            Snackbar.make(logByQuestionLayout, R.string.msg_answer_updated_success, Snackbar.LENGTH_LONG).show()
    }

    private fun exportLog() {
        if (ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            exportFn()
        } else {
            ExportPermissionDialogFragment {
                ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
            }.show(requireActivity().supportFragmentManager,
                    ExportPermissionDialogFragment::class.java.simpleName)
        }
    }
}