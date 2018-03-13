package feelings.guide.ui.log;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import feelings.guide.R;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.RecyclerViewCursorAdapter;
import feelings.guide.util.DateTimeUtil;

import static feelings.guide.answer.AnswerContract.COLUMN_ANSWER;
import static feelings.guide.answer.AnswerContract.COLUMN_DATE_TIME;
import static feelings.guide.answer.AnswerContract.COLUMN_QUESTION_ID;
import static feelings.guide.util.DateTimeUtil.DB_FORMATTER;
import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

/**
 * Used on
 * - answer log by one question
 * - full answer log (for all questions)
 */
class AnswerLogAdapter extends RecyclerViewCursorAdapter<AnswerLogAdapter.AnswerLogHolder> {

    private final Context context;
    private final boolean isFull;
    private final String dateTimeFormat;

    static final class AnswerLogHolder extends RecyclerView.ViewHolder {
        TextView dateTime;
        TextView question;
        TextView answer;

        AnswerLogHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.answer_log_date_time);
            question = itemView.findViewById(R.id.answer_log_question);
            answer = itemView.findViewById(R.id.answer_log_answer);
        }
    }

    AnswerLogAdapter(Context context, boolean isFull, Cursor cursor) {
        super(null);
        this.context = context;
        this.isFull = isFull;
        swapCursor(cursor);
        this.dateTimeFormat = DateTimeUtil.getDateTimeFormat(context);
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
        long questionId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID));
        LocalDateTime dateTime = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                DB_FORMATTER);
        String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));

        holder.dateTime.setText(dateTime.format(ofPattern(dateTimeFormat)));
        if (holder.question != null) {
            holder.question.setText(QuestionService.getQuestionText(context, questionId));
        }
        holder.answer.setText(answerText);
    }
}
