package feelings.guide.answer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import feelings.guide.db.DbHelper
import feelings.guide.question.COLUMN_IS_DELETED
import feelings.guide.question.COLUMN_IS_HIDDEN
import feelings.guide.question.QUESTION_TABLE
import feelings.guide.util.DB_FORMATTER
import org.threeten.bp.LocalDateTime

internal object AnswerStore {

    fun saveAnswer(context: Context, answer: Answer): Boolean = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply {
            if (answer.id > 0) {
                // id is already set when previously deleted answer is being restored
                put(_ID, answer.id)
            }
            put(COLUMN_QUESTION_ID, answer.questionId)
            put(COLUMN_DATE_TIME, answer.dateTime.format(DB_FORMATTER))
            put(COLUMN_ANSWER, answer.answerText)
        }
        return when (val newRowId = it.insert(ANSWER_TABLE, null, values)) {
            -1L -> false
            else -> {
                answer.id = newRowId
                true
            }
        }
    }

    fun updateAnswer(context: Context, answer: Answer): Boolean = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply { put(COLUMN_ANSWER, answer.answerText) }
        val count = it.update(ANSWER_TABLE, values, "$_ID = ${answer.id}", null)
        return count == 1
    }

    fun getAnswers(context: Context, questionId: Long = -1): Cursor {
        val projection = arrayOf(_ID, COLUMN_QUESTION_ID, COLUMN_DATE_TIME, COLUMN_ANSWER)
        val selection = if (questionId > -1) "$COLUMN_QUESTION_ID = $questionId" else null
        return DbHelper.getInstance(context).readableDatabase.query(
            ANSWER_TABLE, projection, selection, null, null, null,
            "$COLUMN_DATE_TIME DESC"
        )
    }

    fun getById(context: Context, id: Long): Answer? = DbHelper.getInstance(context).readableDatabase.use {
        it.query(ANSWER_TABLE, ALL_COLUMNS, "$_ID = $id", null, null, null, null)
            .use { cursor -> return if (cursor.moveToFirst()) mapFromCursor(cursor) else null }
    }

    fun deleteAll(context: Context) = DbHelper.getInstance(context).writableDatabase.use {
        it.delete(ANSWER_TABLE, null, null)
    }

    fun deleteById(context: Context, id: Long) = DbHelper.getInstance(context).writableDatabase.use {
        it.delete(ANSWER_TABLE, "$_ID = $id", null)
    }

    fun deleteByQuestionId(context: Context, questionId: Long) = DbHelper.getInstance(context).writableDatabase.use {
        it.delete(ANSWER_TABLE, "$COLUMN_QUESTION_ID = $questionId", null)
    }

    fun deleteForDeletedQuestions(context: Context) = DbHelper.getInstance(context).writableDatabase.use {
        it.execSQL(
            """delete from $ANSWER_TABLE where $COLUMN_QUESTION_ID in (
                |select $_ID from $QUESTION_TABLE where $COLUMN_IS_DELETED = 1 or $COLUMN_IS_HIDDEN = 1)""".trimMargin()
        )
    }

    fun hasAnswers(context: Context, questionId: Long): Boolean =
        getAnswers(context, questionId).use { cursor -> return cursor.moveToFirst() }

    fun mapFromCursor(cursor: Cursor): Answer {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID))
        val questionId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
        val dateTime = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME)), DB_FORMATTER)
        val answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))

        return Answer(id, questionId, dateTime, answerText)
    }
}
