package feelings.guide.ui.log;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import feelings.guide.R;
import feelings.guide.answer.Answer;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.RecyclerViewCursorAdapter;
import feelings.guide.util.DateTimeUtil;

import static android.provider.BaseColumns._ID;
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
        final TextView dateTimeView;
        final TextView questionView;
        final TextView answerView;

        AnswerLogHolder(View itemView) {
            super(itemView);
            dateTimeView = itemView.findViewById(R.id.answer_log_date_time);
            questionView = itemView.findViewById(R.id.answer_log_question);
            answerView = itemView.findViewById(R.id.answer_log_answer);
        }
    }
    AnswerLogAdapter(BaseActivity activity, boolean isFull, long questionId) {
        super(activity);
        this.isFull = isFull;
        this.questionId = questionId;
        this.dateTimeFormat = DateTimeUtil.INSTANCE.getDateTimeFormat(activity);
        refresh();
    }

    @Override
    @NonNull
    public AnswerLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int itemViewId = isFull
                ? R.layout.answer_log_full_item
                : R.layout.answer_log_by_question_item;
        View view = LayoutInflater.from(activity).inflate(itemViewId, parent, false);
        return new AnswerLogHolder(view);
    }

    @Override
    protected void onBindViewHolder(AnswerLogHolder holder, Cursor cursor) {
        Answer answer = AnswerStore.INSTANCE.mapFromCursor(cursor);

        holder.dateTimeView.setText(answer.getDateTime().format(ofPattern(dateTimeFormat)));
        if (holder.questionView != null) {
            //in case of full log
            holder.questionView.setText(QuestionService.INSTANCE.getQuestionText(activity, answer.getQuestionId()));
        }
        holder.answerView.setText(answer.getAnswerText());
    }

    void refresh() {
        Cursor cursor = AnswerStore.INSTANCE.getAnswers(activity, isFull ? -1 : questionId);
        swapCursor(cursor);
    }

    Answer getByPosition(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return AnswerStore.INSTANCE.mapFromCursor(cursor);
    }

    int getPositionById(long answerId) {
        Cursor cursor = getCursor();
        while (cursor.moveToNext()) {
            if (answerId == cursor.getLong(cursor.getColumnIndexOrThrow(_ID))) {
                return cursor.getPosition();
            }
        }
        return -1;
    }
}
