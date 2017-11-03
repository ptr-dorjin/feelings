package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import feelings.guide.R;


public class QuestionEditDialogFragment extends DialogFragment {

    interface QuestionEditDialogListener {
        void onSaveClick(DialogFragment dialogFragment);
    }

    private QuestionEditDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.question_edit_dialog, null))
                .setTitle(R.string.title_edit_question_dialog)
                .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onSaveClick(QuestionEditDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setCancelable(false);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof QuestionEditDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement QuestionEditDialogListener");
        }
        listener = (QuestionEditDialogListener) context;
    }
}
