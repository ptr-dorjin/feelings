package feelings.guide.ui.question

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

internal interface QuestionDeleteDialogListener {
    fun onDeleteConfirmed()
}

/**
 * Must stay public
 */
class QuestionDeleteDialogFragment : DialogFragment() {

    private lateinit var listener: QuestionDeleteDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
        .setMessage(R.string.title_confirm_delete_question_dialog)
        .setPositiveButton(R.string.btn_delete) { _, _ -> listener.onDeleteConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is QuestionDeleteDialogListener -> listener = context
            else -> throw RuntimeException("${context.javaClass} must implement ${QuestionDeleteDialogListener::class.java}")
        }
    }
}
