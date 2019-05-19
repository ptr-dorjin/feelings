package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import feelings.guide.R;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.Question;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.log.AnswerLogActivity;
import feelings.guide.ui.settings.SettingsActivity;
import feelings.guide.util.ToastUtil;

import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.guide.FeelingsApplication.REFRESH_QUESTIONS_RESULT_KEY;
import static feelings.guide.FeelingsApplication.SETTINGS_REQUEST_CODE;
import static feelings.guide.question.QuestionService.FEELINGS_ID;

public class QuestionsActivity extends BaseActivity implements
        QuestionEditDialogFragment.QuestionEditDialogListener,
        QuestionDeleteDialogFragment.QuestionDeleteDialogListener,
        QuestionHideDialogFragment.QuestionHideDialogListener,
        QuestionClearLogDialogFragment.QuestionClearLogDialogListener {

    private static final String TAG = QuestionsActivity.class.getSimpleName();

    private QuestionsAdapter adapter;
    private FloatingActionButton fab;
    private Long changeQuestionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);

        setUpFab();
        setUpRv();
    }

    private void setUpFab() {
        fab = findViewById(R.id.question_add);
        fab.setOnClickListener(v -> showEditQuestionDialog(null));
    }

    private void setUpRv() {
        RecyclerView rv = findViewById(R.id.questions_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionsAdapter(this);
        adapter.setHasStableIds(true);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.questions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_log:
                showAnswerLog(null);
                return true;
            case R.id.show_settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPopupMenu(final View v) {
        PopupMenu popup = new PopupMenu(this, v);
        final long questionId = (long) v.getTag(R.id.tag_question_id);
        final boolean isUser = (boolean) v.getTag(R.id.tag_is_user);
        popup.inflate(isUser
                ? R.menu.questions_popup_menu_user
                : questionId == FEELINGS_ID
                ? R.menu.questions_popup_menu_feelings
                : R.menu.questions_popup_menu_built_in);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.show_log_by_question:
                    showAnswerLog(questionId);
                    return true;
                case R.id.edit:
                    showEditQuestionDialog(questionId);
                    return true;
                case R.id.delete:
                    showDeleteConfirmation(questionId);
                    return true;
                case R.id.hide:
                    showHideConfirmation(questionId);
                    return true;
                case R.id.clear_log:
                    showClearLogConfirmation(questionId);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void showEditQuestionDialog(Long questionId) {
        changeQuestionId = questionId;
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment dialogFragment = new QuestionEditDialogFragment();
        dialogFragment.setCancelable(false);
        dialogFragment.show(fragmentManager, QuestionEditDialogFragment.class.getSimpleName());
        fragmentManager.executePendingTransactions(); // to fetch inflated dialog
        setUpEditDialog(questionId, dialogFragment);
    }

    private void setUpEditDialog(Long questionId, DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        if (dialog == null) {
            Log.w(TAG, "Edit answer dialog is null");
            return;
        }
        EditText questionEditText = dialog.findViewById(R.id.question_text_edit);
        final Button saveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

        if (questionId != null) {
            questionEditText.setText(QuestionService.getQuestionText(this, questionId));
        } else {
            saveButton.setEnabled(false);
        }
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    private void showDeleteConfirmation(long questionId) {
        changeQuestionId = questionId;
        DialogFragment dialogFragment = new QuestionDeleteDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), QuestionDeleteDialogFragment.class.getSimpleName());
    }

    private void showHideConfirmation(long questionId) {
        changeQuestionId = questionId;
        DialogFragment dialogFragment = new QuestionHideDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), QuestionHideDialogFragment.class.getSimpleName());
    }

    private void showClearLogConfirmation(long questionId) {
        changeQuestionId = questionId;
        DialogFragment dialogFragment = new QuestionClearLogDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), QuestionClearLogDialogFragment.class.getSimpleName());
    }

    private void showAnswerLog(Long questionId) {
        Intent intent = new Intent(this, AnswerLogActivity.class);
        if (questionId != null) {
            intent.putExtra(QUESTION_ID_PARAM, questionId);
        }
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean refresh = data.getBooleanExtra(REFRESH_QUESTIONS_RESULT_KEY, false);
            if (refresh) {
                adapter.refreshAll();
            }
        }
    }

    @Override
    public void onSaveClick(DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        if (dialog == null) {
            Log.w(TAG, "Create/Edit answer dialog is null");
            return;
        }
        EditText questionEditText = dialog.findViewById(R.id.question_text_edit);
        String text = questionEditText.getText().toString().trim();
        if (text.isEmpty()) {
            ToastUtil.showLong(this, getString(R.string.msg_question_text_empty));
            return;
        }

        if (changeQuestionId == null) {
            performServiceAction(QuestionService.createQuestion(this, new Question(text)) != -1,
                    R.string.msg_question_create_success,
                    R.string.msg_question_create_error);
        } else {
            performServiceAction(QuestionService.updateQuestion(this, new Question(changeQuestionId, text)),
                    R.string.msg_question_edit_success,
                    R.string.msg_question_edit_error);
        }
        adapter.refreshAll();
    }

    @Override
    public void onDeleteConfirmed() {
        performServiceAction(QuestionService.deleteQuestion(this, changeQuestionId),
                R.string.msg_question_delete_success,
                R.string.msg_question_delete_error);
        adapter.refreshAll();
        // also clear the log
        if (QuestionService.hasAnswers(this, changeQuestionId)) {
            DialogFragment dialogFragment = new QuestionClearLogDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), QuestionClearLogDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void onHideConfirmed() {
        performServiceAction(QuestionService.hideQuestion(this, changeQuestionId),
                R.string.msg_question_hide_success,
                R.string.msg_question_hide_error);
        adapter.refreshAll();
    }

    @Override
    public void onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(this, changeQuestionId);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_by_question_success));
    }

    private void performServiceAction(boolean success, int successMessageId, int errorMessageId) {
        if (success) {
            ToastUtil.showShort(this, getString(successMessageId));
        } else {
            ToastUtil.showLong(this, getString(errorMessageId));
        }
    }
}
