package feelings.guide.ui.log;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import feelings.guide.R;


public class ClearLogDeletedDialogFragment extends DialogFragment {

    interface ClearLogDeletedDialogListener {
        void onClearLogDeletedConfirmed();
    }

    private ClearLogDeletedDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_clear_log_deleted_dialog)
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onClearLogDeletedConfirmed();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof ClearLogDeletedDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + ClearLogDeletedDialogListener.class);
        }
        listener = (ClearLogDeletedDialogListener) context;
    }
}
