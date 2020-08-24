package feelings.guide.ui.log.full

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

/**
 * Must stay public
 */
class ClearLogDeletedDialogFragment(private val onClearLogDeletedConfirmed: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.title_confirm_clear_log_deleted_dialog)
            .setPositiveButton(R.string.btn_delete) { _, _ -> onClearLogDeletedConfirmed() }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
            .create()
}
