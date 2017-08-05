package feelings.helper.question;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feelings.helper.R;

public class QuestionService {
    private static final String TAG = "QuestionService";

    private static List<Question> questions = new ArrayList<>();
    private static Map<Integer, Question> questionsMap = new HashMap<>();
    private static volatile boolean initialized = false;

    private static void init(Context context) {
        if (!initialized) {
            synchronized (QuestionService.class) {
                if (!initialized) {
                    questions.add(new Question(1, context.getString(R.string.q_feelings)));
                    questions.add(new Question(2, context.getString(R.string.q_insincerity)));
                    questions.add(new Question(3, context.getString(R.string.q_gratitude)));
                    questions.add(new Question(4, context.getString(R.string.q_preach)));
                    questions.add(new Question(5, context.getString(R.string.q_lie)));
                    questions.add(new Question(6, context.getString(R.string.q_irresponsibility)));
                    questions.add(new Question(7, context.getString(R.string.q_do_body)));
                    questions.add(new Question(8, context.getString(R.string.q_do_close)));
                    questions.add(new Question(9, context.getString(R.string.q_do_others)));

                    for (Question question : questions) {
                        questionsMap.put(question.getId(), question);
                    }
                    initialized = true;
                }
            }
        }
    }

    public static String getQuestionText(Context context, int questionId) {
        init(context);
        Question question = questionsMap.get(questionId);
        if (question != null) {
            return question.getText();
        } else {
            Log.e(TAG, "Unexpected questionId=" + questionId);
            return "Couldn't get question text";
        }
    }

    public static List<Question> getAllQuestions(Context context) {
        init(context);
        Collections.sort(questions);
        return questions;
    }
}
