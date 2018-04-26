package feelings.guide.ui.log;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import feelings.guide.R;


public class ClearLogFullDialogFragment extends DialogFragment {

    interface ClearLogFullDialogListener {
        void onClearLogFullConfirmed();
    }

    private ClearLogFullDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_clear_log_full_dialog)
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onClearLogFullConfirmed();
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
        if (!(context instanceof ClearLogFullDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + ClearLogFullDialogListener.class);
        }
        listener = (ClearLogFullDialogListener) context;
    }
}
