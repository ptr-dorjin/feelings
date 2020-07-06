package feelings.guide.data.question

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuestionDao {

    @Query("select * from question where _id = :questionId")
    fun getById(questionId: Long): Question

    @Query("select * from question where is_deleted = 0 and is_hidden = 0 order by _id asc")
    fun getAll(): LiveData<List<Question>>

    @Insert
    suspend fun createQuestion(question: Question): Long

    /**
     * Only for user's question
     */
    @Update(entity = Question::class)
    suspend fun updateQuestion(questionUpdate: QuestionUpdate): Int

    /**
     * Actually doesn't delete, but marks question as deleted
     * Only for user's question
     */
    @Query("update question set is_deleted = 1 where _id = :questionId and is_user = 1")
    suspend fun deleteQuestion(questionId: Long): Int

    /**
     * Only for built-in questions
     */
    @Query("update question set is_hidden = 1 where _id = :questionId and is_user = 0")
    suspend fun hideQuestion(questionId: Long): Int

    /**
     * Only for built-in questions
     */
    @Query("update question set is_hidden = 0 where is_user = 0")
    suspend fun restoreHidden()

    @Query("update question set text = :text where code = :code")
    suspend fun changeLanguage(code: String, text: String)
}