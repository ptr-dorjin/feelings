package feelings.guide.data.answer

import androidx.lifecycle.LiveData

class AnswerRepository(private val answerDao: AnswerDao) {

    fun getAllAnswers(): LiveData<List<Answer>> {
        return answerDao.getAllAnswers()
    }

    fun getAnswersByQuestionId(questionId: Long): LiveData<List<Answer>> {
        return answerDao.getAnswersByQuestionId(questionId)
    }

    fun getById(id: Long): Answer {
        return answerDao.getById(id)
    }

    suspend fun saveAnswer(answer: Answer): Long {
        return answerDao.saveAnswer(answer)
    }

    suspend fun updateAnswer(id: Long, answerText: String): Int {
        return answerDao.updateAnswer(id, answerText)
    }

    suspend fun deleteAnswer(id: Long) {
        return answerDao.deleteById(id)
    }

    suspend fun deleteAll() {
        return answerDao.deleteAll()
    }

    suspend fun deleteForQuestion(questionId: Long) {
        return answerDao.deleteByQuestionId(questionId)
    }

    suspend fun deleteForDeletedQuestions() {
        return answerDao.deleteForDeletedQuestions()
    }
}
