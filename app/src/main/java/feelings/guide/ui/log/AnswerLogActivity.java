package feelings.guide.ui.log;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import feelings.guide.R;
import feelings.guide.answer.Answer;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.question.QuestionClearLogDialogFragment;
import feelings.guide.util.ToastUtil;

import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;

public class AnswerLogActivity extends BaseActivity implements
        ClearLogFullDialogFragment.ClearLogFullDialogListener,
        ClearLogDeletedDialogFragment.ClearLogDeletedDialogListener,
        QuestionClearLogDialogFragment.QuestionClearLogDialogListener,
        AnswerLogSwipeCallback.AnswerLogSwipeListener {

    private static final long DEFAULT_QUESTION_ID = -1;
    private long questionId;
    private boolean isFull;
    private AnswerLogAdapter adapter;
    private Answer lastDeleted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionId = getIntent().getLongExtra(QUESTION_ID_PARAM, DEFAULT_QUESTION_ID);
        isFull = questionId == DEFAULT_QUESTION_ID;

        setContentView(isFull
                ? R.layout.full_answer_log_activity
                : R.layout.answer_log_by_question_activity);

        RecyclerView recyclerView = findViewById(R.id.answer_log_recycler_view);
        TextView emptyLogText = findViewById(R.id.answer_log_empty);

        adapter = new AnswerLogAdapter(this, isFull, questionId);
        boolean notEmpty = adapter.isNotEmpty();
        recyclerView.setVisibility(notEmpty ? View.VISIBLE : View.GONE);
        emptyLogText.setVisibility(notEmpty ? View.GONE : View.VISIBLE);
        if (notEmpty) {
            setUpRecyclerView(recyclerView);
        }
        fillQuestionText();
    }

    private void setUpRecyclerView(RecyclerView rv) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        rv.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new AnswerLogSwipeCallback(this, this));
        itemTouchHelper.attachToRecyclerView(rv);
    }

    private void fillQuestionText() {
        if (!isFull) {
            TextView questionText = findViewById(R.id.question_text_on_answer_log);
            questionText.setText(QuestionService.getQuestionText(this, questionId));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.changeCursor(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (adapter.isNotEmpty()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(isFull
                    ? R.menu.answer_log_full_menu
                    : R.menu.answer_log_by_question_menu,
                    menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_log_full) {
            return showClearLogFullConfirmation();
        } else if (item.getItemId() == R.id.clear_log_deleted) {
            return showClearLogDeletedConfirmation();
        } else if (item.getItemId() == R.id.clear_log_by_question) {
            return showClearLogByQuestionConfirmation();
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean showClearLogFullConfirmation() {
        DialogFragment dialogFragment = new ClearLogFullDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), ClearLogFullDialogFragment.class.getSimpleName());
        return true;
    }

    private boolean showClearLogDeletedConfirmation() {
        DialogFragment dialogFragment = new ClearLogDeletedDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), ClearLogDeletedDialogFragment.class.getSimpleName());
        return true;
    }

    private boolean showClearLogByQuestionConfirmation() {
        DialogFragment dialogFragment = new QuestionClearLogDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), QuestionClearLogDialogFragment.class.getSimpleName());
        return false;
    }

    @Override
    public void onClearLogFullConfirmed() {
        AnswerStore.deleteAll(this);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_full_success));
        adapter.refresh();
    }

    @Override
    public void onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(this);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_deleted_success));
        adapter.refresh();
    }

    @Override
    public void onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(this, questionId);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_by_question_success));
        adapter.refresh();
    }

    @Override
    public void onDeleteAnswer(int position) {
        lastDeleted = adapter.getByPosition(position);
        AnswerStore.deleteById(this, lastDeleted.getId());
        //notifying adapter only about 1 position change does not work for some reason, so update everything as a workaround
        adapter.refresh();
        showUndoDeleteSnackbar();
    }

    private void showUndoDeleteSnackbar() {
        View view = findViewById(R.id.answer_log_view);
        Snackbar snackbar = Snackbar.make(view, R.string.msg_answer_deleted_success, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        AnswerStore.saveAnswer(this, lastDeleted);
        adapter.refresh();
    }

    @Override
    public void onEditAnswer(int position) {

    }
}
