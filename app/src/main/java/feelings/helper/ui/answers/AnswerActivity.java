package feelings.helper.ui.answers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;

import static feelings.helper.ui.questions.QuestionsActivity.QUESTION_ID_PARAM;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        int questionId = getIntent().getIntExtra(QUESTION_ID_PARAM, 0);

        TextView questionView = (TextView) findViewById(R.id.question_text_on_answer);
        questionView.setText(QuestionService.getQuestionText(this, questionId));
    }
}
