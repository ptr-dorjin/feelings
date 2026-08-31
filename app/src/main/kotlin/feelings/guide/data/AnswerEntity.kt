package feelings.guide.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "answer",
    indices = [Index("question_id"), Index("date_time")],
)
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "question_id")
    val questionId: Long,
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime,
    @ColumnInfo(name = "answer")
    val answerText: String?,
)
