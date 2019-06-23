package feelings.guide.ui.log

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

internal interface ClearLogFullDialogListener {
    fun onClearLogFullConfirmed()
}

/**
 * Must stay public
 */
class ClearLogFullDialogFragment : DialogFragment() {

    private lateinit var listener: ClearLogFullDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
        .setMessage(R.string.title_confirm_clear_log_full_dialog)
        .setPositiveButton(R.string.btn_delete) { _, _ -> listener.onClearLogFullConfirmed() }
        .setNegativeButton(R.string.btn_cancel) { _, _ -> dismiss() }
        .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is ClearLogFullDialogListener -> listener = context
            else -> throw RuntimeException("${context.javaClass} must implement ${ClearLogFullDialogListener::class.java}")
        }
    }
}
