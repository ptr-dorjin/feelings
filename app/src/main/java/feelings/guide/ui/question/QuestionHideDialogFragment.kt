package feelings.guide.ui.question

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

internal interface QuestionHideDialogListener {
    fun onHideConfirmed()
}

/**
 * Must stay public
 */
class QuestionHideDialogFragment : DialogFragment() {

    private lateinit var listener: QuestionHideDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
        .setMessage(R.string.title_confirm_hide_question_dialog)
        .setPositiveButton(R.string.btn_hide) { _, _ -> listener.onHideConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is QuestionHideDialogListener -> listener = context
            else -> throw RuntimeException("${context.javaClass} must implement ${QuestionHideDialogListener::class.java}")
        }
    }
}
