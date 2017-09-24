package feelings.helper.ui.log;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import feelings.helper.R;
import feelings.helper.question.QuestionService;

import static feelings.helper.answer.AnswerContract.COLUMN_NAME_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.util.DateTimeUtil.ANSWER_LOG_FORMATTER;
import static feelings.helper.util.DateTimeUtil.DB_FORMATTER;

/**
 * Used on
 * - answer log by one question
 * - full answer log (for all questions)
 */
class AnswerLogAdapter extends RecyclerViewCursorAdapter<AnswerLogAdapter.AnswerLogHolder> {

    private final Context context;
    private final boolean isFull;

    static final class AnswerLogHolder extends RecyclerView.ViewHolder {
        TextView dateTime;
        TextView question;
        TextView answer;

        AnswerLogHolder(View itemView) {
            super(itemView);
            dateTime = (TextView) itemView.findViewById(R.id.answer_log_date_time);
            question = (TextView) itemView.findViewById(R.id.answer_log_question);
            answer = (TextView) itemView.findViewById(R.id.answer_log_answer);
        }
    }

    AnswerLogAdapter(Context context, boolean isFull, Cursor cursor) {
        super(null);
        this.context = context;
        this.isFull = isFull;
        swapCursor(cursor);
    }

    @Override
    public AnswerLogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemViewId = isFull
                ? R.layout.full_answer_log_item
                : R.layout.answer_log_by_question_item;
        View view = LayoutInflater.from(context).inflate(itemViewId, parent, false);
        return new AnswerLogHolder(view);
    }

    @Override
    protected void onBindViewHolder(AnswerLogHolder holder, Cursor cursor) {
        int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
        LocalDateTime dateTime = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE_TIME)),
                DB_FORMATTER);
        String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ANSWER));

        holder.dateTime.setText(dateTime.format(ANSWER_LOG_FORMATTER));
        if (holder.question != null) {
            holder.question.setText(QuestionService.getQuestionText(context, questionId));
        }
        holder.answer.setText(answerText);
    }
}
