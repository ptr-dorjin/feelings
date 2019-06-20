package feelings.guide.ui.question

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

internal interface QuestionEditDialogListener {
    fun onSaveClick(dialogFragment: DialogFragment)
}

/**
 * Must stay public
 */
class QuestionEditDialogFragment : DialogFragment() {

    private lateinit var listener: QuestionEditDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
        .setView(View.inflate(activity, R.layout.question_edit_dialog, null))
        .setTitle(R.string.title_edit_question_dialog)
        .setPositiveButton(R.string.btn_save) { _, _ -> listener.onSaveClick(this@QuestionEditDialogFragment) }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .setCancelable(false)
        .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is QuestionEditDialogListener -> listener = context
            else -> throw RuntimeException("${context.javaClass} must implement ${QuestionEditDialogListener::class.java}")
        }
    }
}
