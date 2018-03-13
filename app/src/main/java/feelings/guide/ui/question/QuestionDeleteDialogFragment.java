package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import feelings.guide.R;


public class QuestionDeleteDialogFragment extends DialogFragment {

    interface QuestionDeleteDialogListener {
        void onDeleteConfirmed();
    }

    private QuestionDeleteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_delete_question_dialog)
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDeleteConfirmed();
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
        if (!(context instanceof QuestionDeleteDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement QuestionDeleteDialogListener");
        }
        listener = (QuestionDeleteDialogListener) context;
    }
}
