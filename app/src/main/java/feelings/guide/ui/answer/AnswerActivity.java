package feelings.guide.ui.answer;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

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

import static feelings.guide.FeelingsApplication.QUESTION_ID_PARAM;
import static java.util.Arrays.asList;

public class AnswerActivity extends BaseActivity {

    private long questionId;
    private EditText answerText;
    private List<String> feelingsGroups = new ArrayList<>();
    private Map<String, List<String>> mapFeelingsByGroup = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        questionId = getIntent().getLongExtra(QUESTION_ID_PARAM, -1);
        Question question = QuestionService.getById(this, questionId);

        setUpQuestionText(question);
        setUpQuestionDescription(question);
        setUpAnswerText();
        setUpFeelingsList();
    }

    private void setUpQuestionText(Question question) {
        TextView questionText = findViewById(R.id.question_text_on_answer);
        questionText.setText(question.getText());
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
        if (questionId != QuestionService.FEELINGS_ID) {
            answerText.requestFocus();
        }
    }

    private void setUpFeelingsList() {
        ExpandableListView feelingsListView = findViewById(R.id.feelings_list_view);
        if (questionId == QuestionService.FEELINGS_ID) {
            setUpFeelingsGroup(R.string.anger, R.array.anger_array);
            setUpFeelingsGroup(R.string.fear, R.array.fear_array);
            setUpFeelingsGroup(R.string.sadness, R.array.sadness_array);
            setUpFeelingsGroup(R.string.joy, R.array.joy_array);
            setUpFeelingsGroup(R.string.love, R.array.love_array);

            final FeelingsExpandableListAdapter adapter = new FeelingsExpandableListAdapter(this, feelingsGroups, mapFeelingsByGroup);
            feelingsListView.setAdapter(adapter);
            feelingsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    String selected = (String) adapter.getChild(groupPosition, childPosition);
                    Editable answer = answerText.getText();
                    if (!answer.toString().trim().isEmpty()) {
                        answer.append(", ");
                    }
                    answer.append(selected);
                    return true;
                }
            });
            feelingsListView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpFeelingsGroup(int feelingsGroupId, int feelingsArrayId) {
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
            if (isInvalid()) {
                return false;
            }
            boolean saved = AnswerStore.saveAnswer(this,
                    new Answer(questionId, LocalDateTime.now(), answerText.getText().toString().trim()));
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
        if (answerText.getText().toString().trim().isEmpty()) {
            ToastUtil.showShortTop(this, getString(R.string.msg_answer_text_empty));
            return true;
        }
        return false;
    }

}
