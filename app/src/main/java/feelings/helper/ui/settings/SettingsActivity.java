package feelings.helper.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;
import feelings.helper.settings.Settings;
import feelings.helper.settings.SettingsStore;
import feelings.helper.util.ToastUtil;

import static feelings.helper.ui.questions.QuestionsActivity.QUESTION_ID_PARAM;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        int questionId = getIntent().getIntExtra(QUESTION_ID_PARAM, 0);

        TextView questionView = (TextView) findViewById(R.id.settings_question_text);
        questionView.setText(QuestionService.getQuestionText(this, questionId));

        Settings settings = SettingsStore.getSettings(this, questionId);
        if (settings != null) {
            ToastUtil.showError("Settings are not null: " + settings, this);
        } else {
            ToastUtil.showError("Settings are null!", this);
        }
    }
}
