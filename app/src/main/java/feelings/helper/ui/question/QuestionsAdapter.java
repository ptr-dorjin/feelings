package feelings.helper.ui.question;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import feelings.helper.R;
import feelings.helper.question.QuestionService;
import feelings.helper.ui.RecyclerViewCursorAdapter;
import feelings.helper.ui.answer.AnswerActivity;

import static android.provider.BaseColumns._ID;
import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.helper.question.QuestionContract.COLUMN_TEXT;

class QuestionsAdapter extends RecyclerViewCursorAdapter<QuestionsAdapter.QuestionViewHolder> {

    private AppCompatActivity context;

    final class QuestionViewHolder extends RecyclerView.ViewHolder {
        long questionId;
        TextView questionText;
        ImageButton popupMenu;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = (TextView) itemView.findViewById(R.id.question_text_on_card);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popupMenu);

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
        holder.questionId = questionId;
        holder.questionText.setText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)));
        holder.popupMenu.setTag(questionId);
    }

    void refreshAll() {
        swapCursor(QuestionService.getAllQuestions(context));
    }
}
