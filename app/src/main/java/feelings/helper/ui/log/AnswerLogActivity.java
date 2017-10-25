package feelings.helper.ui.log;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import feelings.helper.R;
import feelings.helper.answer.AnswerStore;
import feelings.helper.question.QuestionService;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

public class AnswerLogActivity extends AppCompatActivity {

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.answer_log_recycler_view);
        TextView emptyLogText = (TextView) findViewById(R.id.answer_log_empty);

        if (cursor.getCount() > 0) {
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
        TextView questionText= (TextView) findViewById(R.id.question_text_on_answer_log);
        questionText.setText(QuestionService.getQuestionText(this, questionId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.changeCursor(null);
        }
    }
}
