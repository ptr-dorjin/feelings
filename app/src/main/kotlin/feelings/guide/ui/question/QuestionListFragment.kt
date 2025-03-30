package feelings.guide.ui.question

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.answer.AnswerStore
import feelings.guide.databinding.QuestionEditDialogBinding
import feelings.guide.databinding.QuestionListBinding
import feelings.guide.question.Question
import feelings.guide.question.QuestionService

class QuestionListFragment : Fragment() {

    private lateinit var adapter: QuestionsAdapter
    private var changeQuestionId: Long? = null
    private var popup: PopupMenu? = null
    private var _binding: QuestionListBinding? = null
    private var _bindingEditQuestion: QuestionEditDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val bindingEditQuestion get() = _bindingEditQuestion!!

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.questions_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.show_log -> {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        (requireActivity() as QuestionListActivity).navigateToAnswerLogFull()
                    }
                    true
                }

                R.id.show_settings -> {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        (requireActivity() as QuestionListActivity).navigateToSettings()
                    }
                    true
                }

                else -> false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuestionListBinding.inflate(
            inflater, container, false
        )
        requireActivity().addMenuProvider(menuProvider)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        // to avoid "Activity has leaked window" error, popup should be dismissed
        popup?.dismiss()
        popup = null
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpFab()
        setUpRv()
    }

    private fun setUpFab() {
        binding.questionFab.setOnClickListener { showEditQuestionDialog(null) }
    }

    private fun setUpRv() {
        binding.questionRV.layoutManager = LinearLayoutManager(context)
        adapter = QuestionsAdapter(requireContext(), this::questionClickCallback)
        adapter.setHasStableIds(true)
        binding.questionRV.adapter = adapter
    }

    private fun questionClickCallback(questionId: Long) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            (requireActivity() as QuestionListActivity).navigateToAnswer(questionId)
        }
    }

    fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val questionId = view.getTag(R.id.tag_question_id) as Long
        val isUser = view.getTag(R.id.tag_is_user) as Boolean
        popup.inflate(
            when {
                isUser -> R.menu.questions_popup_menu_user
                questionId == QuestionService.FEELINGS_ID -> R.menu.questions_popup_menu_feelings
                else -> R.menu.questions_popup_menu_built_in
            }
        )
        popup.setOnMenuItemClickListener {
            popup.dismiss()
            when (it.itemId) {
                R.id.show_log_by_question -> {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        (requireActivity() as QuestionListActivity).navigateToAnswerLogByQuestion(
                            questionId
                        )
                    }
                    true
                }

                R.id.edit -> {
                    showEditQuestionDialog(questionId)
                    true
                }

                R.id.delete -> {
                    showDeleteConfirmation(questionId)
                    true
                }

                R.id.hide -> {
                    showHideConfirmation(questionId)
                    true
                }

                R.id.clear_log -> {
                    showClearLogConfirmation(questionId)
                    true
                }

                else -> false
            }
        }
        popup.show()
        this.popup = popup
    }

    private fun showEditQuestionDialog(questionId: Long?) {
        changeQuestionId = questionId
        val fragmentManager = requireActivity().supportFragmentManager
        val dialogFragment = QuestionEditDialogFragment(
            this::onSaveClick
        )
        // This works here, but not in QuestionEditDialogFragment
        dialogFragment.isCancelable = false
        dialogFragment.show(
            fragmentManager,
            QuestionEditDialogFragment::class.java.simpleName
        )
        fragmentManager.executePendingTransactions() // to fetch inflated dialog
        // This works here, but not in QuestionEditDialogFragment
        setUpEditDialog(questionId, dialogFragment)
    }

    private fun setUpEditDialog(questionId: Long?, dialogFragment: QuestionEditDialogFragment) {
        val dialog: Dialog = dialogFragment.dialog!!
        val questionTextEdit = dialog.findViewById<EditText>(R.id.questionTextEdit)
        val saveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

        if (questionId != null)
            questionTextEdit.setText(
                QuestionService.getQuestionText(
                    requireContext(), questionId
                )
            )
        else saveButton.isEnabled = false

        questionTextEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                saveButton.isEnabled = !TextUtils.isEmpty(s) && s.trim().isNotEmpty()
            }
        })
    }

    private fun showDeleteConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionDeleteDialogFragment(this::onDeleteConfirmed).show(
            requireActivity().supportFragmentManager,
            QuestionDeleteDialogFragment::class.java.simpleName
        )
    }

    private fun showHideConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionHideDialogFragment(this::onHideConfirmed).show(
            requireActivity().supportFragmentManager,
            QuestionHideDialogFragment::class.java.simpleName
        )
    }

    private fun showClearLogConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionClearLogDialogFragment(this::onClearLogByQuestionConfirmed).show(
            requireActivity().supportFragmentManager,
            QuestionClearLogDialogFragment::class.java.simpleName
        )
    }

    private fun onSaveClick(text: String) {
        if (text.isEmpty()) {
            Snackbar.make(
                binding.questionsListLayout,
                R.string.msg_question_text_empty,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        if (changeQuestionId == null) performServiceAction(
            QuestionService.createQuestion(requireContext(), Question(text)) != -1L,
            R.string.msg_question_create_success,
            R.string.msg_question_create_error
        ) else performServiceAction(
            QuestionService.updateQuestion(requireContext(), Question(changeQuestionId!!, text)),
            R.string.msg_question_edit_success,
            R.string.msg_question_edit_error
        )
        adapter.refreshAll()
    }

    private fun onDeleteConfirmed() {
        performServiceAction(
            QuestionService.deleteQuestion(requireContext(), changeQuestionId!!),
            R.string.msg_question_delete_success,
            R.string.msg_question_delete_error
        )
        adapter.refreshAll()
        // also clear the log
        if (QuestionService.hasAnswers(requireContext(), changeQuestionId!!)) {
            val dialogFragment = QuestionClearLogDialogFragment(this::onClearLogByQuestionConfirmed)
            dialogFragment.show(
                requireActivity().supportFragmentManager,
                QuestionClearLogDialogFragment::class.java.simpleName
            )
        }
    }

    private fun onHideConfirmed() {
        performServiceAction(
            QuestionService.hideQuestion(requireContext(), changeQuestionId!!),
            R.string.msg_question_hide_success,
            R.string.msg_question_hide_error
        )
        adapter.refreshAll()
    }

    private fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(requireContext(), changeQuestionId!!)
        Snackbar.make(
            binding.questionsListLayout,
            R.string.msg_clear_log_by_question_success,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun performServiceAction(success: Boolean, successMessageId: Int, errorMessageId: Int) =
        if (success) Snackbar.make(
            binding.questionsListLayout,
            successMessageId,
            Snackbar.LENGTH_LONG
        )
            .show()
        else Snackbar.make(binding.questionsListLayout, errorMessageId, Snackbar.LENGTH_LONG).show()

    internal fun onReturnFromSettings(needToRefresh: Boolean) {
        if (needToRefresh) adapter.refreshAll()
    }

    internal fun onReturnFromAddAnswer(answerIsAdded: Boolean) {
        if (answerIsAdded) Snackbar.make(
            binding.questionsListLayout,
            R.string.msg_answer_added_success,
            Snackbar.LENGTH_LONG
        ).show()
    }
}