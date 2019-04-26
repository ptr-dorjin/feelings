package feelings.guide.ui.log;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.threeten.bp.LocalDateTime;

import feelings.guide.R;
import feelings.guide.answer.Answer;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.ui.RecyclerViewCursorAdapter;
import feelings.guide.util.DateTimeUtil;
import feelings.guide.util.ToastUtil;

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

    private final boolean isFull;
    private final String dateTimeFormat;

    private Answer recentlyDeletedAnswer;

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
    AnswerLogAdapter(BaseActivity activity, boolean isFull, Cursor cursor) {
        super(activity, null);
        this.isFull = isFull;
        swapCursor(cursor);
        this.dateTimeFormat = DateTimeUtil.getDateTimeFormat(activity);
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
        long questionId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID));
        LocalDateTime dateTime = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                DB_FORMATTER);
        String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));

        holder.dateTimeView.setText(dateTime.format(ofPattern(dateTimeFormat)));
        if (holder.questionView != null) {
            //in case of full log
            holder.questionView.setText(QuestionService.getQuestionText(activity, questionId));
        }
        holder.answerView.setText(answerText);
    }

    void refreshAll(Cursor cursor) {
        swapCursor(cursor);
    }

    void deleteAnswer(int position) {
//        recentlyDeletedAnswer = getCursor().get(position);
//        mRecentlyDeletedItemPosition = position;
//        mListItems.remove(position);
//        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    void editAnswer(int position) {
        ToastUtil.showShort(activity, "Edit answer");
    }

    private void showUndoSnackbar() {
        View view = activity.findViewById(R.id.answer_log_view);
        Snackbar snackbar = Snackbar.make(view, R.string.msg_answer_deleted_success, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
//        mListItems.add(mRecentlyDeletedItemPosition,
//                mRecentlyDeletedItem);
//        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
}
