package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
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
        LayoutInflater inflater = activity.getLayoutInflater();
        return new AlertDialog.Builder(activity)
                .setView(inflater.inflate(R.layout.question_edit_dialog, null))
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
                .setCancelable(false)
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof QuestionEditDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + QuestionEditDialogListener.class);
        }
        listener = (QuestionEditDialogListener) context;
    }
}
