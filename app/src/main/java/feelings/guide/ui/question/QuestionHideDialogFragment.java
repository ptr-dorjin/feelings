package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import feelings.guide.R;


public class QuestionHideDialogFragment extends DialogFragment {

    interface QuestionHideDialogListener {
        void onHideClick();
    }

    private QuestionHideDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_hide_question_dialog)
                .setPositiveButton(R.string.btn_hide, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onHideClick();
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
        if (!(context instanceof QuestionHideDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement QuestionHideDialogListener");
        }
        listener = (QuestionHideDialogListener) context;
    }
}
