package feelings.guide.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val CODE_FEELINGS = "q_feelings"

@Entity(tableName = "question")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    /** Only set for built-in questions. */
    @ColumnInfo(name = "code")
    val code: String? = null,
    @ColumnInfo(name = "text", defaultValue = "")
    val text: String = "",
    @ColumnInfo(name = "is_user", defaultValue = "0")
    val isUser: Boolean = true,
    /** Can be set for user questions by the user, or for built-in questions on hide/restore. */
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false,
    /** Only for built-in questions. */
    @ColumnInfo(name = "is_hidden", defaultValue = "0")
    val isHidden: Boolean = false,
) {
    val isFeelings: Boolean get() = code == CODE_FEELINGS
}
