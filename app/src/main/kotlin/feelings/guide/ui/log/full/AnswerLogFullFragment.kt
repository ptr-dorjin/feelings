package feelings.guide.ui.log.full

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.log.AnswerLogSwipeCallback
import kotlinx.android.synthetic.main.answer_log_full.*


class AnswerLogFullFragment(private val exportFn: () -> Unit) : Fragment() {

    private lateinit var adapter: AnswerLogFullAdapter
    private var lastDeleted: Answer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.answer_log_full, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        adapter = AnswerLogFullAdapter(requireContext())
        val notEmpty = adapter.isNotEmpty
        logFullRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        logFullEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        logFullRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
                logFullRV.context,
                layoutManager.orientation
        )
        logFullRV.addItemDecoration(dividerItemDecoration)

        logFullRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(
                AnswerLogSwipeCallback(
                        requireContext(),
                        this::onDeleteAnswer,
                        this::onEditAnswer
                )
        )
        itemTouchHelper.attachToRecyclerView(logFullRV)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (adapter.isNotEmpty) {
            inflater.inflate(R.menu.answer_log_full_menu, menu)
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
        R.id.export_log -> {
            exportFn()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showClearLogFullConfirmation() {
        ClearLogFullDialogFragment(this::onClearLogFullConfirmed)
                .show(requireActivity().supportFragmentManager,
                        ClearLogFullDialogFragment::class.java.simpleName)
    }

    private fun showClearLogDeletedConfirmation() {
        ClearLogDeletedDialogFragment(this::onClearLogDeletedConfirmed)
                .show(requireActivity().supportFragmentManager,
                        ClearLogDeletedDialogFragment::class.java.simpleName)
    }

    private fun onClearLogFullConfirmed() {
        AnswerStore.deleteAll(requireContext())
        Snackbar.make(logFullLayout, R.string.msg_clear_log_full_success, Snackbar.LENGTH_LONG)
                .show()
        adapter.refresh()
    }

    private fun onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(requireContext())
        Snackbar.make(logFullLayout, R.string.msg_clear_log_deleted_success, Snackbar.LENGTH_LONG)
                .show()
        adapter.refresh()
    }

    private fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(requireContext(), lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        Snackbar.make(logFullLayout, R.string.msg_answer_deleted_success, Snackbar.LENGTH_LONG)
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
        logFullRV.adapter = adapter

        //scroll to the answer
        if (answerId > 0) {
            val position = adapter.getPositionById(answerId)
            if (position > -1) {
                logFullRV.scrollToPosition(position)
            }
        }
        if (answerIsUpdated)
            Snackbar.make(logFullLayout, R.string.msg_answer_updated_success, Snackbar.LENGTH_LONG).show()
    }
}