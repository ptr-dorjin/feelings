package feelings.guide.ui.question

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R
import kotlinx.android.synthetic.main.question_edit_dialog.*

/**
 * Must stay public
 */
class QuestionEditDialogFragment(private val onSaveClick: (String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setView(View.inflate(activity, R.layout.question_edit_dialog, null))
            .setTitle(R.string.title_edit_question_dialog)
            .setPositiveButton(R.string.btn_save) { _, _ -> onSaveClick(dialog!!.questionTextEdit.text.toString().trim()) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
            .create()
}
