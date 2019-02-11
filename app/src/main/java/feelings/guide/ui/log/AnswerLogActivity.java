package feelings.guide.ui.log;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import feelings.guide.R;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.question.QuestionClearLogDialogFragment;
import feelings.guide.util.ToastUtil;

import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;

public class AnswerLogActivity extends BaseActivity implements
        ClearLogFullDialogFragment.ClearLogFullDialogListener,
        ClearLogDeletedDialogFragment.ClearLogDeletedDialogListener,
        QuestionClearLogDialogFragment.QuestionClearLogDialogListener {

    private static final long DEFAULT_QUESTION_ID = -1;
    private long questionId;
    private boolean isFull;
    private Cursor cursor;
    private AnswerLogAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionId = getIntent().getLongExtra(QUESTION_ID_PARAM, DEFAULT_QUESTION_ID);
        isFull = questionId == DEFAULT_QUESTION_ID;

        setContentView(isFull
                ? R.layout.full_answer_log_activity
                : R.layout.answer_log_by_question_activity);

        cursor = isFull
                ? AnswerStore.getAll(this)
                : AnswerStore.getByQuestionId(this, questionId);

        RecyclerView recyclerView = findViewById(R.id.answer_log_recycler_view);
        TextView emptyLogText = findViewById(R.id.answer_log_empty);

        if (!isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLogText.setVisibility(View.GONE);
            setUpRecyclerView(recyclerView);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyLogText.setVisibility(View.VISIBLE);
        }

        fillQuestionText();
    }

    private void setUpRecyclerView(RecyclerView rv) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new AnswerLogAdapter(this, isFull, cursor);
        rv.setAdapter(adapter);
    }

    private void fillQuestionText() {
        if (isFull) {
            return;
        }
        TextView questionText = findViewById(R.id.question_text_on_answer_log);
        questionText.setText(QuestionService.getQuestionText(this, questionId));
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
        if (!isEmpty()) {
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
        cursor = AnswerStore.getAll(this);
        adapter.refreshAll(cursor);
    }

    @Override
    public void onClearLogDeletedConfirmed() {
        AnswerStore.deleteForDeletedQuestions(this);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_deleted_success));
        cursor = AnswerStore.getAll(this);
        adapter.refreshAll(cursor);
    }

    @Override
    public void onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(this, questionId);
        ToastUtil.showShort(this, getString(R.string.msg_clear_log_by_question_success));
        cursor = AnswerStore.getByQuestionId(this, questionId);
        adapter.refreshAll(cursor);
    }

    private boolean isEmpty() {
        return cursor.getCount() == 0;
    }
}
