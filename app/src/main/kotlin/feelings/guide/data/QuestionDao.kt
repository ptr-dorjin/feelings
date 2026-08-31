package feelings.guide.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    /**
     * Built-in questions are ranked explicitly so upgrading installs — which get new built-ins
     * inserted with higher `_id`s than existing user questions — still show them in the intended
     * order. User questions (code IS NULL) and any unranked/deprecated built-in fall into the
     * last bucket and sort by creation order.
     */
    @Query(
        """SELECT * FROM question WHERE is_deleted = 0 AND is_hidden = 0
           ORDER BY
             CASE code
               WHEN 'q_feelings' THEN 0
               WHEN 'q_gratitude' THEN 1
               WHEN 'q_do_body' THEN 2
               WHEN 'q_do_mind' THEN 3
               WHEN 'q_do_close' THEN 4
               WHEN 'q_do_others' THEN 5
               ELSE 6
             END,
             _id ASC"""
    )
    fun observeVisible(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question ORDER BY _id ASC")
    fun observeAll(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question ORDER BY _id ASC")
    suspend fun getAllAsList(): List<QuestionEntity>

    @Query("SELECT * FROM question WHERE _id = :id")
    fun observeById(id: Long): Flow<QuestionEntity?>

    @Query("SELECT * FROM question WHERE _id = :id")
    suspend fun getById(id: Long): QuestionEntity?

    @Query("SELECT COUNT(*) FROM question WHERE is_user = 0 AND is_hidden = 1")
    fun observeHiddenBuiltInCount(): Flow<Int>

    @Insert
    suspend fun insert(question: QuestionEntity): Long

    @Update
    suspend fun update(question: QuestionEntity)

    /** Only for user questions; soft-delete, matching legacy semantics. */
    @Query("UPDATE question SET is_deleted = 1 WHERE _id = :id AND is_user = 1")
    suspend fun softDeleteUserQuestion(id: Long)

    /** Only for built-in questions. */
    @Query("UPDATE question SET is_hidden = 1 WHERE _id = :id AND is_user = 0")
    suspend fun hideBuiltIn(id: Long)

    @Query("UPDATE question SET is_hidden = 0 WHERE is_user = 0")
    suspend fun restoreHidden()

    @Query("UPDATE question SET text = :text WHERE code = :code")
    suspend fun relocalize(code: String, text: String)
}
