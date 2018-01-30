package feelings.guide.ui.question;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import feelings.guide.R;
import feelings.guide.question.Question;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.log.AnswerLogActivity;
import feelings.guide.ui.settings.SettingsActivity;
import feelings.guide.util.ToastUtil;

import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;

public class QuestionsActivity extends BaseActivity implements
        QuestionEditDialogFragment.QuestionEditDialogListener,
        QuestionDeleteDialogFragment.QuestionDeleteDialogListener {

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditQuestionDialog(null);
            }
        });
    }

    private void setUpRv() {
        RecyclerView rv = findViewById(R.id.questions_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionsAdapter(this);
        adapter.setHasStableIds(true);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
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
        if (item.getItemId() == R.id.show_log) {
            return showAnswerLog(null);
        } else if (item.getItemId() == R.id.show_settings) {
            return showSettings();
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showPopupMenu(final View v) {
        PopupMenu popup = new PopupMenu(this, v);
        final long questionId = (long) v.getTag(R.id.tag_question_id);
        final boolean isUser = (boolean) v.getTag(R.id.tag_is_user);
        popup.inflate(isUser
                ? R.menu.questions_popup_menu_user
                : R.menu.questions_popup_menu_system);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.show_log_by_question:
                        return showAnswerLog(questionId);
                    case R.id.edit:
                        return showEditQuestionDialog(questionId);
                    case R.id.delete:
                        return showDeleteConfirmation(questionId);
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private boolean showEditQuestionDialog(Long questionId) {
        changeQuestionId = questionId;
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment dialogFragment = new QuestionEditDialogFragment();
        dialogFragment.setCancelable(false);
        dialogFragment.show(fragmentManager, QuestionEditDialogFragment.class.getSimpleName());
        fragmentManager.executePendingTransactions(); // to fetch inflated dialog
        setUpEditDialog(questionId, dialogFragment);
        return true;
    }

    private void setUpEditDialog(Long questionId, DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        EditText questionEditText = dialog.findViewById(R.id.question_text_edit);
        final Button saveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

        if (questionId != null) {
            questionEditText.setText(QuestionService.getQuestionText(this, questionId));
        } else {
            saveButton.setEnabled(false);
        }
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    private boolean showDeleteConfirmation(long questionId) {
        changeQuestionId = questionId;
        DialogFragment dialogFragment = new QuestionDeleteDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), QuestionDeleteDialogFragment.class.getSimpleName());
        return true;
    }

    private boolean showAnswerLog(Long questionId) {
        Intent intent = new Intent(this, AnswerLogActivity.class);
        if (questionId != null) {
            intent.putExtra(QUESTION_ID_PARAM, questionId);
        }
        startActivity(intent);
        return true;
    }

    private boolean showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onSaveClick(DialogFragment dialogFragment) {
        EditText questionEditText = dialogFragment.getDialog().findViewById(R.id.question_text_edit);
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
    public void onDeleteClick(DialogFragment dialogFragment) {
        performServiceAction(QuestionService.deleteQuestion(this, changeQuestionId),
                R.string.msg_question_delete_success,
                R.string.msg_question_delete_error);
        adapter.refreshAll();
    }

    private void performServiceAction(boolean success, int successMessageId, int errorMessageId) {
        if (success) {
            ToastUtil.showShort(this, getString(successMessageId));
        } else {
            ToastUtil.showLong(this, getString(errorMessageId));
        }
    }
}
