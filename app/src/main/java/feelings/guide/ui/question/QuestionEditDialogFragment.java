package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import feelings.guide.R;

/**
 * Must stay public
 */
public class QuestionEditDialogFragment extends DialogFragment {

    interface QuestionEditDialogListener {
        void onSaveClick(DialogFragment dialogFragment);
    }

    private QuestionEditDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(Objects.requireNonNull(activity))
                .setView(View.inflate(activity, R.layout.question_edit_dialog, null))
                .setTitle(R.string.title_edit_question_dialog)
                .setPositiveButton(R.string.btn_save, (dialog, id) -> listener.onSaveClick(QuestionEditDialogFragment.this))
                .setNegativeButton(R.string.btn_cancel, (dialog, id) -> dismiss())
                .setCancelable(false)
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof QuestionEditDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + QuestionEditDialogListener.class);
        }
        listener = (QuestionEditDialogListener) context;
    }
}
