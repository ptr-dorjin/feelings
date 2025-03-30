package feelings.guide.ui.log.full

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.databinding.AnswerLogFullBinding
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.log.AnswerLogSwipeCallback


class AnswerLogFullFragment(private val exportFn: () -> Unit) : Fragment() {

    private lateinit var adapter: AnswerLogFullAdapter
    private var lastDeleted: Answer? = null
    private var _binding: AnswerLogFullBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            if (adapter.isNotEmpty) {
                menuInflater.inflate(R.menu.answer_log_full_menu, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
            when (menuItem.itemId) {
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

                else -> false
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnswerLogFullBinding.inflate(
            inflater, container, false
        )
        requireActivity().addMenuProvider(menuProvider)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AnswerLogFullAdapter(requireContext())
        val notEmpty = adapter.isNotEmpty
        binding.logFullRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        binding.logFullEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.logFullRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            binding.logFullRV.context,
            layoutManager.orientation
        )
        binding.logFullRV.addItemDecoration(dividerItemDecoration)

        binding.logFullRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(
            AnswerLogSwipeCallback(
                requireContext(),
                this::onDeleteAnswer,
                this::onEditAnswer
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.logFullRV)
    }

    private fun showClearLogFullConfirmation() {
        ClearLogFullDialogFragment(this::onClearLogFullConfirmed)
            .show(
                requireActivity().supportFragmentManager,
                ClearLogFullDialogFragment::class.java.simpleName
            )
    }

    private fun showClearLogDeletedConfirmation() {
        ClearLogDeletedDialogFragment(this::onClearLogDeletedConfirmed)
            .show(
                requireActivity().supportFragmentManager,
                ClearLogDeletedDialogFragment::class.java.simpleName
            )
    }

    private fun onClearLogFullConfirmed() {
        AnswerStore.deleteAll(requireContext())
        Snackbar.make(
            binding.logFullLayout,
            R.string.msg_clear_log_full_success,
            Snackbar.LENGTH_LONG
        )
            .show()
        adapter.refresh()
    }

    private fun onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(requireContext())
        Snackbar.make(
            binding.logFullLayout,
            R.string.msg_clear_log_deleted_success,
            Snackbar.LENGTH_LONG
        )
            .show()
        adapter.refresh()
    }

    private fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(requireContext(), lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        Snackbar.make(
            binding.logFullLayout,
            R.string.msg_answer_deleted_success,
            Snackbar.LENGTH_LONG
        )
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
        binding.logFullRV.adapter = adapter

        //scroll to the answer
        if (answerId > 0) {
            val position = adapter.getPositionById(answerId)
            if (position > -1) {
                binding.logFullRV.scrollToPosition(position)
            }
        }
        if (answerIsUpdated)
            Snackbar.make(
                binding.logFullLayout,
                R.string.msg_answer_updated_success,
                Snackbar.LENGTH_LONG
            )
                .show()
    }
}