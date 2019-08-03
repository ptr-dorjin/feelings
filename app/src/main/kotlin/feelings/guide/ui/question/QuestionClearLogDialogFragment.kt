package feelings.guide.ui.question

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

/**
 * Must stay public
 */
class QuestionClearLogDialogFragment(private val onClearLogByQuestionConfirmed: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
        .setMessage(R.string.title_confirm_clear_log_by_question_dialog)
        .setPositiveButton(R.string.btn_delete) { _, _ -> onClearLogByQuestionConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()
}
