package feelings.helper.ui.answers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import feelings.helper.R;
import feelings.helper.answer.Answer;
import feelings.helper.answer.AnswerStore;
import feelings.helper.question.QuestionService;
import feelings.helper.util.ToastUtil;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;

public class AnswerActivity extends AppCompatActivity {

    private int questionId;
    private EditText answerText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        questionId = getIntent().getIntExtra(QUESTION_ID_PARAM, 0);

        TextView questionView = (TextView) findViewById(R.id.question_text_on_answer);
        questionView.setText(QuestionService.getQuestionText(this, questionId));

        answerText = (EditText) findViewById(R.id.answer_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Save button is pressed
        if (item.getItemId() == R.id.save) {
            if (isInvalid()) {
                return false;
            }
            boolean saved = AnswerStore.saveAnswer(this,
                    new Answer(questionId, LocalDateTime.now(), answerText.getText().toString()));
            if (saved) {
                ToastUtil.showShort(this, getString(R.string.msg_answer_saved_success));
                finish();
            } else {
                ToastUtil.showLong(this, getString(R.string.msg_answer_saved_error));
            }
            return saved;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Validation
     */
    private boolean isInvalid() {
        if (answerText.getText().length() == 0) {
            ToastUtil.showLong(this, getString(R.string.msg_answer_text_empty));
            return true;
        }
        return false;
    }

}
