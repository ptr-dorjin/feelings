package feelings.guide.data.question

import android.util.SparseArray
import feelings.guide.R

const val FEELINGS_ID: Long = 1

const val QUESTION_TABLE = "question"
const val COLUMN_CODE = "code"
const val COLUMN_TEXT = "text"
const val COLUMN_DESCRIPTION = "description"
const val COLUMN_IS_USER = "is_user"
const val COLUMN_IS_DELETED = "is_deleted"
const val COLUMN_IS_HIDDEN = "is_hidden"

data class QuestionCode internal constructor(
        val code: Int,
        val textId: Int
)

object QuestionContract {
    val QUESTION_CODE_MAP = SparseArray<QuestionCode>()

    init {
        map(QuestionCode(R.string.q_feelings, R.string.q_text_feelings))
        map(QuestionCode(R.string.q_insincerity, R.string.q_text_insincerity))
        map(QuestionCode(R.string.q_gratitude, R.string.q_text_gratitude))
        map(QuestionCode(R.string.q_preach, R.string.q_text_preach))
        map(QuestionCode(R.string.q_lie, R.string.q_text_lie))
        map(QuestionCode(R.string.q_irresponsibility, R.string.q_text_irresponsibility))
        map(QuestionCode(R.string.q_do_body, R.string.q_text_do_body))
        map(QuestionCode(R.string.q_do_close, R.string.q_text_do_close))
        map(QuestionCode(R.string.q_do_others, R.string.q_text_do_others))
    }

    private fun map(qc: QuestionCode) = QUESTION_CODE_MAP.put(qc.code, qc)
}
