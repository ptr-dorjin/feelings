package feelings.guide.data.question

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class Question(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = BaseColumns._ID)
        var id: Long = 0,

        /**
         * only for built-in questions
         */
        @ColumnInfo(name = "code")
        var code: String? = null,

        @ColumnInfo(name = "text")
        var text: String? = null,

        @ColumnInfo(name = "is_user")
        var isUser: Boolean = true,

        /**
         * can be set for user questions - by user, for built-in questions - only during app update
         */
        @ColumnInfo(name = "is_deleted")
        var isDeleted: Boolean = false,

        /**
         * only for built-in questions. can be done by user
         */
        @ColumnInfo(name = "is_hidden")
        var isHidden: Boolean = false
) {
    constructor(text: String) : this() {
        this.text = text
        isUser = true
    }

    constructor(id: Long, text: String) : this() {
        this.id = id
        this.text = text
        isUser = true
    }
}

data class QuestionUpdate(
        val id: Long,
        val text: String
)