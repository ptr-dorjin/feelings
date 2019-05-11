package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import feelings.guide.R;

/**
 * Must stay public
 */
public class QuestionDeleteDialogFragment extends DialogFragment {

    interface QuestionDeleteDialogListener {
        void onDeleteConfirmed();
    }

    private QuestionDeleteDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(Objects.requireNonNull(activity))
                .setMessage(R.string.title_confirm_delete_question_dialog)
                .setPositiveButton(R.string.btn_delete, (dialog, id) -> listener.onDeleteConfirmed())
                .setNegativeButton(R.string.btn_cancel, (dialog, id) -> dismiss())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof QuestionDeleteDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + QuestionDeleteDialogListener.class);
        }
        listener = (QuestionDeleteDialogListener) context;
    }
}
