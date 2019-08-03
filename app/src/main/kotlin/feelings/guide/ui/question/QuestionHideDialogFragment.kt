package feelings.guide.ui.question

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

/**
 * Must stay public
 */
class QuestionHideDialogFragment(private val onHideConfirmed: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
        .setMessage(R.string.title_confirm_hide_question_dialog)
        .setPositiveButton(R.string.btn_hide) { _, _ -> onHideConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()
}
