package feelings.guide.question;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import feelings.guide.answer.AnswerStore;

public class QuestionService {

    private static final String TAG = QuestionService.class.getSimpleName();

    public static final long FEELINGS_ID = 1;

    public static String getQuestionText(Context context, long questionId) {
        Question question = QuestionStore.getById(context, questionId);
        return question != null
                ? question.getText()
                : "Couldn't get question text";
    }

    public static long createQuestion(Context context, Question question) {
        return QuestionStore.createQuestion(context, question);
    }

    public static boolean updateQuestion(Context context, Question question) {
        if (question.getId() == FEELINGS_ID) {
            Log.e(TAG, "updateQuestion: attempt to update feelings question!");
            return false;
        }
        return QuestionStore.updateQuestion(context, question);
    }

    public static boolean deleteQuestion(Context context, long questionId) {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "deleteQuestion: attempt to delete feelings question!");
            return false;
        }
        return QuestionStore.deleteQuestion(context, questionId);
    }

    public static boolean hideQuestion(Context context, long questionId) {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "hideQuestion: attempt to hide feelings question!");
            return false;
        }
        return QuestionStore.hideQuestion(context, questionId);
    }

    public static void restoreHidden(Context context) {
        QuestionStore.restoreHidden(context);
    }

    public static Cursor getAllQuestions(Context context) {
        return QuestionStore.getAll(context);
    }

    public static Question getById(Context context, long questionId) {
        Question question = QuestionStore.getById(context, questionId);
        if (question == null) {
            Log.e(TAG, "getById: couldn't find question by id=" + questionId);
        }
        return question;
    }

    public static boolean changeLanguage(Context context) {
        return QuestionStore.changeLanguage(context);
    }

    public static boolean hasAnswers(Context context, long questionId) {
        return AnswerStore.hasAnswers(context, questionId);
    }
}
