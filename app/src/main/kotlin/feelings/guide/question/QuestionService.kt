package feelings.guide.question

import android.content.Context
import android.database.Cursor
import android.util.Log

import feelings.guide.answer.AnswerStore

object QuestionService {

    private val TAG = QuestionService::class.java.simpleName

    const val FEELINGS_ID: Long = 1

    fun getQuestionText(context: Context, questionId: Long): String? {
        val question = QuestionStore.getById(context, questionId)
        return if (question != null)
            question.text
        else
            "Couldn't get question text"
    }

    fun createQuestion(context: Context, question: Question): Long {
        return QuestionStore.createQuestion(context, question)
    }

    fun updateQuestion(context: Context, question: Question): Boolean {
        if (question.id == FEELINGS_ID) {
            Log.e(TAG, "updateQuestion: attempt to update feelings question!")
            return false
        }
        return QuestionStore.updateQuestion(context, question)
    }

    fun deleteQuestion(context: Context, questionId: Long): Boolean {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "deleteQuestion: attempt to delete feelings question!")
            return false
        }
        return QuestionStore.deleteQuestion(context, questionId)
    }

    fun hideQuestion(context: Context, questionId: Long): Boolean {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "hideQuestion: attempt to hide feelings question!")
            return false
        }
        return QuestionStore.hideQuestion(context, questionId)
    }

    fun restoreHidden(context: Context) {
        QuestionStore.restoreHidden(context)
    }

    fun getAllQuestions(context: Context): Cursor {
        return QuestionStore.getAllVisible(context)
    }

    fun getById(context: Context, questionId: Long): Question? {
        val question = QuestionStore.getById(context, questionId)
        if (question == null) {
            Log.e(TAG, "getById: couldn't find question by id=$questionId")
        }
        return question
    }

    fun changeLanguage(context: Context) {
        QuestionStore.changeLanguage(context)
    }

    fun hasAnswers(context: Context, questionId: Long): Boolean {
        return AnswerStore.hasAnswers(context, questionId)
    }
}
