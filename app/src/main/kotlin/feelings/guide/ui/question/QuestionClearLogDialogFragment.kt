package feelings.guide.ui.question

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

interface QuestionClearLogDialogListener {
    fun onClearLogByQuestionConfirmed()
}

/**
 * Must stay public
 */
class QuestionClearLogDialogFragment : DialogFragment() {

    private lateinit var listener: QuestionClearLogDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
        .setMessage(R.string.title_confirm_clear_log_by_question_dialog)
        .setPositiveButton(R.string.btn_delete) { _, _ -> listener.onClearLogByQuestionConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is QuestionClearLogDialogListener -> listener = context
            else -> throw RuntimeException("${context.javaClass} must implement ${QuestionClearLogDialogListener::class.java}")
        }
    }
}
