package feelings.guide.ui.question

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

/**
 * Must stay public
 */
class QuestionEditDialogFragment(
    private val onSaveClick: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.question_edit_dialog, null)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.title_edit_question_dialog)
            .setPositiveButton(R.string.btn_save) { _, _ ->
                val questionTextEdit = view.findViewById<EditText>(
                    R.id.questionTextEdit
                )
                onSaveClick(
                    questionTextEdit.text.toString().trim()
                )
            }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
            .create()
    }
}
