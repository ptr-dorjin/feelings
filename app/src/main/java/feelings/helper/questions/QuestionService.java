package feelings.helper.questions;

import android.content.Context;
import android.util.SparseArray;

import feelings.helper.R;

public class QuestionService {
    public static Question FEELINGS;
    public static Question INSINCERITY;
    public static Question GRATITUDE;
    public static Question DO_BODY;
    public static Question PREACH;

    private static SparseArray<Question> questions = new SparseArray<>();
    private static volatile boolean initialized = false;

    public static void init(Context context) {
        if (!initialized) {
            synchronized (QuestionService.class) {
                if (!initialized) {
                    FEELINGS = new Question(1, context.getString(R.string.q_feelings));
                    INSINCERITY = new Question(2, context.getString(R.string.q_insincerity));
                    GRATITUDE = new Question(3, context.getString(R.string.q_gratitude));
                    DO_BODY = new Question(4, context.getString(R.string.q_do_body));
                    PREACH = new Question(5, context.getString(R.string.q_preach));

                    questions.append(FEELINGS.getId(), FEELINGS);
                    questions.append(INSINCERITY.getId(), INSINCERITY);
                    questions.append(GRATITUDE.getId(), GRATITUDE);
                    questions.append(DO_BODY.getId(), DO_BODY);
                    questions.append(PREACH.getId(), PREACH);
                    initialized = true;
                }
            }
        }
    }

    public static String getQuestionText(int questionId) {
        checkInit();
        Question question = questions.get(questionId);
        if (question != null) {
            return question.getText();
        } else {
            throw new RuntimeException("Unexpected questionId=" + questionId);
        }
    }

    private static void checkInit() {
        if (!initialized) throw new RuntimeException("QuestionService is not initialized!");
    }

}
