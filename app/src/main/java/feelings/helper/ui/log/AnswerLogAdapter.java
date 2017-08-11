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
import feelings.helper.answer.AnswerStore;
import feelings.helper.question.QuestionService;

import static feelings.helper.answer.AnswerContract.COLUMN_NAME_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.util.DateTimeUtil.DATE_TIME_ANSWER_LOG_FORMATTER;
import static feelings.helper.util.DateTimeUtil.DB_FORMATTER;

class AnswerLogAdapter extends RecyclerViewCursorAdapter<AnswerLogAdapter.AnswerLogHolder> {

    private final Context context;

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

    public AnswerLogAdapter(Context context) {
        super(null);
        this.context = context;
        swapCursor(AnswerStore.getCursor(context));
    }

    @Override
    public AnswerLogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_log_item, parent, false);
        return new AnswerLogHolder(view);
    }

    @Override
    protected void onBindViewHolder(AnswerLogHolder holder, Cursor cursor) {
        int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
        LocalDateTime dateTime = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE_TIME)),
                DB_FORMATTER);
        String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ANSWER));

        holder.dateTime.setText(dateTime.format(DATE_TIME_ANSWER_LOG_FORMATTER));
        holder.question.setText(QuestionService.getQuestionText(context, questionId));
        holder.answer.setText(answerText);
    }
}
