package feelings.guide.ui.answer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feelings.guide.R;
import feelings.guide.answer.Answer;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.Question;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.BaseActivity;
import feelings.guide.util.ToastUtil;

import static feelings.guide.FeelingsApplication.ANSWER_ID_PARAM;
import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.guide.FeelingsApplication.REFRESH_ANSWER_LOG_RESULT_KEY;
import static feelings.guide.FeelingsApplication.UPDATED_ANSWER_ID_RESULT_KEY;
import static java.util.Arrays.asList;

public class AnswerActivity extends BaseActivity {

    private long questionId;
    private Answer answer;
    private EditText answerText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        Intent intent = getIntent();
        questionId = intent != null ? intent.getLongExtra(QUESTION_ID_PARAM, -1) : -1;
        Question question = QuestionService.INSTANCE.getById(this, questionId);

        long answerId = intent != null ? intent.getLongExtra(ANSWER_ID_PARAM, -1) : -1;
        answer = answerId == -1 ? null : AnswerStore.INSTANCE.getById(this, answerId);

        if (question == null) {
            ToastUtil.INSTANCE.showLong(this, "Error: cannot find question");
            return;
        }

        setUpQuestionText(question);
        setUpQuestionDescription(question);
        setUpAnswerText();
        setUpFeelingsList();
    }

    private void setUpQuestionText(Question question) {
        TextView questionText = findViewById(R.id.question_text_on_answer);
        questionText.setText(question.getText());
        questionText.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setUpQuestionDescription(Question question) {
        TextView questionDescription = findViewById(R.id.question_description_on_answer);
        String description = question.getDescription();
        if (description != null && !description.isEmpty()) {
            questionDescription.setText(description);
            questionDescription.setVisibility(View.VISIBLE);
        }
    }

    private void setUpAnswerText() {
        answerText = findViewById(R.id.answer_text);
        if (isEdit()) {
            answerText.setText(answer.getAnswerText());
        }
        if (questionId != QuestionService.FEELINGS_ID) {
            answerText.requestFocus();
        }
    }

    private void setUpFeelingsList() {
        if (questionId == QuestionService.FEELINGS_ID) {
            ExpandableListView feelingsListView = findViewById(R.id.feelings_list_view);

            List<String> feelingsGroups = new ArrayList<>();
            Map<String, List<String>> mapFeelingsByGroup = new HashMap<>();

            setUpFeelingsGroup(R.string.anger, R.array.anger_array, feelingsGroups, mapFeelingsByGroup);
            setUpFeelingsGroup(R.string.fear, R.array.fear_array, feelingsGroups, mapFeelingsByGroup);
            setUpFeelingsGroup(R.string.sadness, R.array.sadness_array, feelingsGroups, mapFeelingsByGroup);
            setUpFeelingsGroup(R.string.joy, R.array.joy_array, feelingsGroups, mapFeelingsByGroup);
            setUpFeelingsGroup(R.string.love, R.array.love_array, feelingsGroups, mapFeelingsByGroup);

            final FeelingsExpandableListAdapter adapter = new FeelingsExpandableListAdapter(this, feelingsGroups, mapFeelingsByGroup);
            feelingsListView.setAdapter(adapter);
            feelingsListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String selected = (String) adapter.getChild(groupPosition, childPosition);
                Editable answer = answerText.getText();
                if (!answer.toString().trim().isEmpty()) {
                    answer.append(", ");
                }
                answer.append(selected);
                return true;
            });
            feelingsListView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpFeelingsGroup(int feelingsGroupId,
                                    int feelingsArrayId,
                                    List<String> feelingsGroups,
                                    Map<String, List<String>> mapFeelingsByGroup) {
        Resources resources = getResources();
        String group = resources.getString(feelingsGroupId);
        feelingsGroups.add(group);
        mapFeelingsByGroup.put(group, asList(resources.getStringArray(feelingsArrayId)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Save button is pressed
        if (item.getItemId() == R.id.save) {
            return save();
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean save() {
        if (isInvalid()) {
            return false;
        }
        String text = answerText.getText().toString().trim();
        boolean isNew = isNew();
        boolean saved;
        if (isNew) {
            answer = new Answer(questionId, LocalDateTime.now(), text);
            saved = AnswerStore.INSTANCE.saveAnswer(this, answer);
        } else {
            answer.setAnswerText(text);
            saved = AnswerStore.INSTANCE.updateAnswer(this, answer);
        }

        if (saved) {
            ToastUtil.INSTANCE.showShort(this, getString(isNew
                    ? R.string.msg_answer_added_success
                    : R.string.msg_answer_updated_success));
            Intent data = new Intent();
            data.putExtra(REFRESH_ANSWER_LOG_RESULT_KEY, true);
            data.putExtra(UPDATED_ANSWER_ID_RESULT_KEY, answer.getId());
            setResult(RESULT_OK, data);
            finish();
        } else {
            ToastUtil.INSTANCE.showLong(this, getString(isNew
                    ? R.string.msg_answer_added_error
                    : R.string.msg_answer_updated_error));
        }
        return saved;
    }

    /**
     * Validation
     */
    private boolean isInvalid() {
        if (answerText.getText().toString().trim().isEmpty()) {
            ToastUtil.INSTANCE.showShortTop(this, getString(R.string.msg_answer_text_empty));
            return true;
        }
        return false;
    }

    private boolean isEdit() {
        return answer != null;
    }

    private boolean isNew() {
        return answer == null;
    }

}
