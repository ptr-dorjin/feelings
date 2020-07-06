package feelings.guide.data.answer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AnswerDao {

    @Query("select * from answer order by date_time desc")
    fun getAllAnswers(): LiveData<List<Answer>>

    @Query("select * from answer where question_id = :questionId order by date_time desc")
    fun getAnswersByQuestionId(questionId: Long): LiveData<List<Answer>>

    @Query("select * from answer where _id = :id")
    fun getById(id: Long): Answer

    @Insert
    suspend fun saveAnswer(answer: Answer): Long

    @Update(entity = Answer::class)
    suspend fun updateAnswer(answerUpdate: AnswerUpdate): Int

    @Delete
    suspend fun delete(answer: Answer)

    @Query("delete from answer where question_id = :questionId")
    suspend fun deleteByQuestionId(questionId: Long)

    @Query("delete from answer where question_id in (select _id from question where is_deleted = 1 or is_hidden = 1)")
    suspend fun deleteForDeletedQuestions()

    @Query("delete from answer")
    suspend fun deleteAll()

    @Query("select exists (select 1 from answer where question_id = :questionId limit 1)")
    fun hasAnswers(questionId: Long): Boolean
}
