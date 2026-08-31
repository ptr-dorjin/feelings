package feelings.guide.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

data class AnswerStat(
    val questionId: Long,
    val count: Int,
    val lastAnsweredAt: LocalDateTime,
)

@Dao
interface AnswerDao {

    @Query("SELECT * FROM answer WHERE question_id = :questionId ORDER BY date_time DESC")
    fun observeByQuestion(questionId: Long): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answer ORDER BY date_time DESC")
    fun observeAll(): Flow<List<AnswerEntity>>

    @Query("SELECT COUNT(*) FROM answer")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT * FROM answer WHERE _id = :id")
    suspend fun getById(id: Long): AnswerEntity?

    @Query(
        """SELECT question_id AS questionId, COUNT(*) AS count, MAX(date_time) AS lastAnsweredAt
           FROM answer GROUP BY question_id"""
    )
    fun observeStats(): Flow<List<AnswerStat>>

    @Query(
        """SELECT * FROM answer
           WHERE (:questionId IS NULL OR question_id = :questionId)
           ORDER BY date_time DESC"""
    )
    suspend fun getForExport(questionId: Long?): List<AnswerEntity>

    @Insert
    suspend fun insert(answer: AnswerEntity): Long

    @Update
    suspend fun update(answer: AnswerEntity)

    @Query("DELETE FROM answer WHERE _id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM answer WHERE question_id = :questionId")
    suspend fun deleteByQuestionId(questionId: Long)

    @Query("DELETE FROM answer")
    suspend fun deleteAll()

    @Query(
        """DELETE FROM answer WHERE question_id IN
           (SELECT _id FROM question WHERE is_deleted = 1 OR is_hidden = 1)"""
    )
    suspend fun deleteForDeletedOrHiddenQuestions()
}
