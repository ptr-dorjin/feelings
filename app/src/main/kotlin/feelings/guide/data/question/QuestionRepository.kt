package feelings.guide.data.question

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import feelings.guide.R
import feelings.guide.data.answer.AnswerDao

private val TAG = QuestionRepository::class.java.simpleName

class QuestionRepository(
        private val questionDao: QuestionDao,
        private val answerDao: AnswerDao
) {

    fun getById(questionId: Long): Question {
        return questionDao.getById(questionId)
    }

    fun getAllQuestions(): LiveData<List<Question>> {
        return questionDao.getAll()
    }

    suspend fun createQuestion(question: Question): Long {
        return questionDao.createQuestion(question)
    }

    suspend fun updateQuestion(questionUpdate: QuestionUpdate): Boolean {
        if (questionUpdate.id == FEELINGS_ID) {
            Log.e(TAG, "updateQuestion: attempt to update feelings question!")
            return false
        }
        return questionDao.updateQuestion(questionUpdate) == 1
    }

    suspend fun deleteQuestion(questionId: Long): Boolean {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "deleteQuestion: attempt to delete feelings question!")
            return false
        }
        return questionDao.deleteQuestion(questionId) == 1
    }

    suspend fun hideQuestion(questionId: Long): Boolean {
        if (questionId == FEELINGS_ID) {
            Log.e(TAG, "hideQuestion: attempt to hide feelings question!")
            return false
        }
        return questionDao.hideQuestion(questionId) == 1
    }

    suspend fun restoreHidden() {
        questionDao.restoreHidden()
    }

    suspend fun changeLanguage(context: Context) {
        changeLanguage(context, R.string.q_feelings)
        changeLanguage(context, R.string.q_gratitude)
        changeLanguage(context, R.string.q_do_body)
        changeLanguage(context, R.string.q_do_close)
        changeLanguage(context, R.string.q_do_others)
        changeLanguage(context, R.string.q_insincerity)
        changeLanguage(context, R.string.q_preach)
        changeLanguage(context, R.string.q_lie)
        changeLanguage(context, R.string.q_irresponsibility)
    }

    private suspend fun changeLanguage(context: Context, code: Int) {
        QuestionContract.QUESTION_CODE_MAP.get(code)?.let {
            questionDao.changeLanguage(
                    context.getString(code),
                    context.getString(it.textId)
            )
        }
    }

    fun hasAnswers(questionId: Long): Boolean {
        return answerDao.hasAnswers(questionId)
    }
}
