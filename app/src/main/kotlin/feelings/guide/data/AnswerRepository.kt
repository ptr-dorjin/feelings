package feelings.guide.data

import feelings.guide.answer.AnswerForExport
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerRepository @Inject constructor(
    private val answerDao: AnswerDao,
    private val questionDao: QuestionDao,
) {
    fun observeByQuestion(questionId: Long): Flow<List<AnswerEntity>> = answerDao.observeByQuestion(questionId)

    fun observeAll(): Flow<List<AnswerEntity>> = answerDao.observeAll()

    suspend fun getById(id: Long): AnswerEntity? = answerDao.getById(id)

    suspend fun saveNewAnswer(questionId: Long, text: String): AnswerEntity {
        val answer = AnswerEntity(questionId = questionId, dateTime = LocalDateTime.now(), answerText = text)
        val id = answerDao.insert(answer)
        return answer.copy(id = id)
    }

    suspend fun updateAnswer(id: Long, text: String) {
        val existing = answerDao.getById(id) ?: return
        answerDao.update(existing.copy(answerText = text))
    }

    /** Re-inserts a previously deleted answer with its original id, for the delete/undo snackbar. */
    suspend fun restoreAnswer(answer: AnswerEntity) {
        answerDao.insert(answer)
    }

    suspend fun deleteById(id: Long) = answerDao.deleteById(id)

    suspend fun clearForQuestion(questionId: Long) = answerDao.deleteByQuestionId(questionId)

    suspend fun clearAll() = answerDao.deleteAll()

    suspend fun clearForDeletedOrHiddenQuestions() = answerDao.deleteForDeletedOrHiddenQuestions()

    suspend fun exportAnswers(questionId: Long?): List<AnswerForExport> {
        val questionTextById = questionDao.getAllAsList().associate { it.id to it.text }
        return answerDao.getForExport(questionId).map { answer ->
            AnswerForExport(
                dateTime = answer.dateTime,
                questionText = questionTextById[answer.questionId] ?: "",
                answerText = answer.answerText ?: "",
            )
        }
    }
}
