package feelings.guide.data.answer

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "answer")
data class Answer(
        @ColumnInfo(name = "question_id")
        val questionId: Long,

        @ColumnInfo(name = "date_time")
        val dateTime: LocalDateTime,

        @ColumnInfo(name = "answer")
        var answerText: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var id: Long = 0

    /**
     * Ctor for cases when ID is known beforehand. Ex: restoring deleted answer
     */
    internal constructor(id: Long, questionId: Long, dateTime: LocalDateTime, answerText: String)
            : this(questionId, dateTime, answerText) {
        this.id = id
    }
}

data class AnswerUpdate(
        val id: Long,
        val answerText: String
)