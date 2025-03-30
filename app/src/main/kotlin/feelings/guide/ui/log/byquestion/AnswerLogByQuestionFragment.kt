package feelings.guide.ui.log.byquestion

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
import feelings.guide.databinding.AnswerLogByQuestionBinding
import feelings.guide.question.QuestionService
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.log.AnswerLogSwipeCallback
import feelings.guide.ui.question.QuestionClearLogDialogFragment


class AnswerLogByQuestionFragment(
    private var questionId: Long,
    private val exportFn: () -> Unit
) : Fragment() {

    private lateinit var adapter: AnswerLogByQuestionAdapter
    private var lastDeleted: Answer? = null
    private var _binding: AnswerLogByQuestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            if (adapter.isNotEmpty) {
                menuInflater.inflate(R.menu.answer_log_by_question_menu, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
            when (menuItem.itemId) {
                R.id.clear_log_by_question -> {
                    showClearLogByQuestionConfirmation()
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
        _binding = AnswerLogByQuestionBinding.inflate(
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
        adapter = AnswerLogByQuestionAdapter(requireContext(), questionId)
        val notEmpty = adapter.isNotEmpty
        binding.logByQuestionRV.visibility = if (notEmpty) View.VISIBLE else View.GONE
        binding.logByQuestionEmptyText.visibility = if (notEmpty) View.GONE else View.VISIBLE
        if (notEmpty)
            setUpRecyclerView()
        fillQuestionText()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.logByQuestionRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            binding.logByQuestionRV.context,
            layoutManager.orientation
        )
        binding.logByQuestionRV.addItemDecoration(dividerItemDecoration)

        binding.logByQuestionRV.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(
            AnswerLogSwipeCallback(
                requireContext(),
                this::onDeleteAnswer,
                this::onEditAnswer
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.logByQuestionRV)
    }

    private fun fillQuestionText() {
        binding.logByQuestionText.text =
            QuestionService.getQuestionText(requireContext(), questionId)
    }

    private fun showClearLogByQuestionConfirmation() {
        QuestionClearLogDialogFragment(this::onClearLogByQuestionConfirmed)
            .show(
                requireActivity().supportFragmentManager,
                QuestionClearLogDialogFragment::class.java.simpleName
            )
    }

    private fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(requireContext(), questionId)
        Snackbar.make(
            binding.logByQuestionLayout,
            R.string.msg_clear_log_by_question_success,
            Snackbar.LENGTH_LONG
        ).show()
        adapter.refresh()
    }

    private fun onDeleteAnswer(position: Int) {
        lastDeleted = adapter.getByPosition(position)
        AnswerStore.deleteById(requireContext(), lastDeleted!!.id)
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh()

        Snackbar.make(
            binding.logByQuestionLayout,
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
        binding.logByQuestionRV.adapter = adapter

        //scroll to the answer
        if (answerId > 0) {
            val position = adapter.getPositionById(answerId)
            if (position > -1) {
                binding.logByQuestionRV.scrollToPosition(position)
            }
        }
        if (answerIsUpdated)
            Snackbar.make(
                binding.logByQuestionLayout,
                R.string.msg_answer_updated_success,
                Snackbar.LENGTH_LONG
            ).show()
    }
}