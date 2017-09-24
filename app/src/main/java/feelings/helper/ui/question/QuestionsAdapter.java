package feelings.helper.ui.question;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import feelings.helper.R;
import feelings.helper.question.Question;
import feelings.helper.question.QuestionService;
import feelings.helper.ui.answer.AnswerActivity;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private AppCompatActivity context;
    private List<Question> questions = new ArrayList<>();

    final class QuestionViewHolder extends RecyclerView.ViewHolder {
        Question currentQuestion;
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
                    intent.putExtra(QUESTION_ID_PARAM, currentQuestion.getId());
                    // set position as a requestCode. This requestCode will be passed to this activity's onActivityResult
                    context.startActivity(intent);
                }
            });
        }
    }

    QuestionsAdapter(AppCompatActivity context) {
        this.context = context;
        questions = QuestionService.getAllQuestions(context);
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        bindCardItem(holder, questions.get(position));
    }

    private void bindCardItem(QuestionViewHolder holder, Question question) {
        holder.currentQuestion = question;
        holder.questionText.setText(question.getText());
        holder.popupMenu.setTag(question.getId());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
