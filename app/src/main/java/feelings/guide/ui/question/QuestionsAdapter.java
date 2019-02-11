package feelings.guide.ui.question;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import feelings.guide.R;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.RecyclerViewCursorAdapter;
import feelings.guide.ui.answer.AnswerActivity;

import static android.provider.BaseColumns._ID;
import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.guide.question.QuestionContract.COLUMN_IS_USER;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;

class QuestionsAdapter extends RecyclerViewCursorAdapter<QuestionsAdapter.QuestionViewHolder> {

    private AppCompatActivity context;

    final class QuestionViewHolder extends RecyclerView.ViewHolder {
        long questionId;
        TextView questionText;
        ImageButton popupMenu;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text_on_card);
            popupMenu = itemView.findViewById(R.id.popupMenu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AnswerActivity.class);
                    intent.putExtra(QUESTION_ID_PARAM, questionId);
                    // set position as a requestCode. This requestCode will be passed to this activity's onActivityResult
                    context.startActivity(intent);
                }
            });
        }
    }

    QuestionsAdapter(AppCompatActivity context) {
        super(null);
        this.context = context;
        refreshAll();
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, Cursor cursor) {
        long questionId = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        boolean isUser = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER));
        holder.questionId = questionId;
        holder.questionText.setText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)));
        holder.popupMenu.setTag(R.id.tag_question_id, questionId);
        holder.popupMenu.setTag(R.id.tag_is_user, isUser);
    }

    void refreshAll() {
        swapCursor(QuestionService.getAllQuestions(context));
    }
}
