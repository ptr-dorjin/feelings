package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import feelings.guide.R;


public class QuestionClearLogDialogFragment extends DialogFragment {

    public interface QuestionClearLogDialogListener {
        void onClearLogByQuestionConfirmed();
    }

    private QuestionClearLogDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new RuntimeException("Host Activity is null");
        }
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.title_confirm_clear_log_by_question_dialog)
                .setPositiveButton(R.string.btn_delete, (dialog, id) -> listener.onClearLogByQuestionConfirmed())
                .setNegativeButton(R.string.btn_cancel, (dialog, id) -> dismiss())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof QuestionClearLogDialogListener)) {
            throw new RuntimeException(context.getClass() + " must implement "
                    + QuestionClearLogDialogListener.class);
        }
        listener = (QuestionClearLogDialogListener) context;
    }
}
