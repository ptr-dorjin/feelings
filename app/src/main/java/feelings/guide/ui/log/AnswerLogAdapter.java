package feelings.guide.ui.log;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import feelings.guide.R;
import feelings.guide.answer.Answer;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.RecyclerViewCursorAdapter;
import feelings.guide.util.DateTimeUtil;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

/**
 * Used on
 * - answer log by one question
 * - full answer log (for all questions)
 */
class AnswerLogAdapter extends RecyclerViewCursorAdapter<AnswerLogAdapter.AnswerLogHolder> {

    private final boolean isFull;
    private final long questionId;
    private final String dateTimeFormat;

    static final class AnswerLogHolder extends RecyclerView.ViewHolder {
        TextView dateTimeView;
        TextView questionView;
        TextView answerView;

        AnswerLogHolder(View itemView) {
            super(itemView);
            dateTimeView = itemView.findViewById(R.id.answer_log_date_time);
            questionView = itemView.findViewById(R.id.answer_log_question);
            answerView = itemView.findViewById(R.id.answer_log_answer);
        }
    }
    AnswerLogAdapter(BaseActivity activity, boolean isFull, long questionId) {
        super(activity, null);
        this.isFull = isFull;
        this.questionId = questionId;
        this.dateTimeFormat = DateTimeUtil.getDateTimeFormat(activity);
        refresh();
    }

    @Override
    @NonNull
    public AnswerLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int itemViewId = isFull
                ? R.layout.full_answer_log_item
                : R.layout.answer_log_by_question_item;
        View view = LayoutInflater.from(activity).inflate(itemViewId, parent, false);
        return new AnswerLogHolder(view);
    }

    @Override
    protected void onBindViewHolder(AnswerLogHolder holder, Cursor cursor) {
        Answer answer = AnswerStore.mapFromCursor(cursor);

        holder.dateTimeView.setText(answer.getDateTime().format(ofPattern(dateTimeFormat)));
        if (holder.questionView != null) {
            //in case of full log
            holder.questionView.setText(QuestionService.getQuestionText(activity, answer.getQuestionId()));
        }
        holder.answerView.setText(answer.getAnswerText());
    }

    void refresh() {
        Cursor cursor = isFull
                ? AnswerStore.getAll(activity)
                : AnswerStore.getByQuestionId(activity, questionId);
        swapCursor(cursor);
    }

    Answer getByPosition(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return AnswerStore.mapFromCursor(cursor);
    }
}
