package feelings.guide.ui.log

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import feelings.guide.R

/**
 * Must stay public
 */
class ExportPermissionDialogFragment(private val onExportPermission: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext())
        .setMessage(R.string.title_export_permission_dialog)
        .setPositiveButton(R.string.btn_permit) { _, _ -> onExportPermission() }
        .create()
}
