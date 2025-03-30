package feelings.guide.question

import android.provider.BaseColumns
import android.util.SparseArray

import feelings.guide.R

const val QUESTION_TABLE = "question"
const val COLUMN_CODE = "code"
const val COLUMN_TEXT = "text"
const val COLUMN_DESCRIPTION = "description"
const val COLUMN_IS_USER = "is_user"
const val COLUMN_IS_DELETED = "is_deleted"
const val COLUMN_IS_HIDDEN = "is_hidden"

val ALL_COLUMNS = arrayOf(
        BaseColumns._ID,
        COLUMN_CODE,
        COLUMN_TEXT,
        COLUMN_DESCRIPTION,
        COLUMN_IS_USER,
        COLUMN_IS_DELETED,
        COLUMN_IS_HIDDEN
)

/**
autoincrement guaranties that there are no collisions in question IDs with any IDs ever before
existed in this table, i.e. IDs are never re-used.
 */
const val SQL_CREATE_QUESTION_TABLE: String = """CREATE TABLE $QUESTION_TABLE (
    ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    $COLUMN_CODE TEXT,
    $COLUMN_TEXT TEXT,
    $COLUMN_DESCRIPTION TEXT,
    $COLUMN_IS_USER INTEGER,
    $COLUMN_IS_DELETED INTEGER,
    $COLUMN_IS_HIDDEN INTEGER
)"""

@ConsistentCopyVisibility
data class QuestionCode internal constructor(
        val code: Int,
        val textId: Int,
        val descriptionId: Int
)

object QuestionContract {
    val QUESTION_CODE_MAP = SparseArray<QuestionCode>()

    init {
        map(QuestionCode(R.string.q_feelings, R.string.q_text_feelings, R.string.q_description_feelings))
        map(QuestionCode(R.string.q_insincerity, R.string.q_text_insincerity, R.string.q_description_insincerity))
        map(QuestionCode(R.string.q_gratitude, R.string.q_text_gratitude, R.string.q_description_gratitude))
        map(QuestionCode(R.string.q_preach, R.string.q_text_preach, R.string.q_description_preach))
        map(QuestionCode(R.string.q_lie, R.string.q_text_lie, R.string.q_description_lie))
        map(QuestionCode(R.string.q_irresponsibility, R.string.q_text_irresponsibility, R.string.q_description_irresponsibility))
        map(QuestionCode(R.string.q_do_body, R.string.q_text_do_body, R.string.q_description_do_body))
        map(QuestionCode(R.string.q_do_close, R.string.q_text_do_close, R.string.q_description_do_close))
        map(QuestionCode(R.string.q_do_others, R.string.q_text_do_others, R.string.q_description_do_others))
    }

    private fun map(qc: QuestionCode) = QUESTION_CODE_MAP.put(qc.code, qc)
}
