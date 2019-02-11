package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import feelings.guide.R;


public class QuestionHideDialogFragment extends DialogFragment {

    interface QuestionHideDialogListener {
        void onHideConfirmed();
    }

    private QuestionHideDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_hide_question_dialog)
                .setPositiveButton(R.string.btn_hide, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onHideConfirmed();
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
            throw new RuntimeException(context.getClass() + " must implement "
                    + QuestionHideDialogListener.class);
        }
        listener = (QuestionHideDialogListener) context;
    }
}
