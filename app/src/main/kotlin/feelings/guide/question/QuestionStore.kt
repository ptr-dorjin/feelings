package feelings.guide.question

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import feelings.guide.R
import feelings.guide.db.DbHelper
import feelings.guide.question.QuestionContract.QUESTION_CODE_MAP

internal object QuestionStore {

    fun getById(context: Context, questionId: Long): Question? = DbHelper.getInstance(context).readableDatabase.use {
        it.query(QUESTION_TABLE, ALL_COLUMNS, "$_ID = $questionId", null, null, null, null)
                .use { cursor -> return if (cursor.moveToFirst()) mapFromCursor(cursor) else null }
    }

    fun mapFromCursor(cursor: Cursor): Question = Question().apply {
        id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID))
        code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE))
        text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
        description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
        isUser = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER))
        isDeleted = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DELETED))
        isHidden = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_HIDDEN))
    }

    /**
     * Only for user's question
     */
    fun createQuestion(context: Context, question: Question): Long = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply {
            put(COLUMN_TEXT, question.text)
            put(COLUMN_IS_USER, question.isUser)
            put(COLUMN_IS_DELETED, false)
            put(COLUMN_IS_HIDDEN, false)
        }
        return it.insert(QUESTION_TABLE, null, values)
    }

    /**
     * Only for user's question
     */
    fun updateQuestion(context: Context, question: Question): Boolean = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply { put(COLUMN_TEXT, question.text) }
        val count = it.update(QUESTION_TABLE, values, "$_ID = ${question.id}", null)
        return count == 1
    }

    /**
     * Actually doesn't delete, but marks question as deleted
     * Only for user's question
     */
    fun deleteQuestion(context: Context, questionId: Long): Boolean = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply { put(COLUMN_IS_DELETED, true) }
        val count = it.update(QUESTION_TABLE, values, "$_ID = $questionId and $COLUMN_IS_USER = 1", null)
        return count == 1
    }

    /**
     * Only for built-in questions
     */
    fun hideQuestion(context: Context, questionId: Long): Boolean = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply { put(COLUMN_IS_HIDDEN, true) }
        val count = it.update(QUESTION_TABLE, values, "$_ID = $questionId and $COLUMN_IS_USER = 0", null)
        return count == 1
    }

    /**
     * Only for built-in questions
     */
    fun restoreHidden(context: Context) = DbHelper.getInstance(context).writableDatabase.use {
        val values = ContentValues().apply { put(COLUMN_IS_HIDDEN, false) }
        it.update(QUESTION_TABLE, values, "$COLUMN_IS_USER = 0", null)
    }

    fun getAllVisible(context: Context): Cursor {
        return DbHelper.getInstance(context).readableDatabase.query(
                QUESTION_TABLE, ALL_COLUMNS, "$COLUMN_IS_DELETED = 0 and $COLUMN_IS_HIDDEN = 0",
                null, null, null, "$_ID ASC"
        )
    }

    fun getAllAsList(context: Context): List<Question> {
        var cursor: Cursor? = null
        try {
            cursor = DbHelper.getInstance(context).readableDatabase.query(
                    QUESTION_TABLE, ALL_COLUMNS, null,
                    null, null, null, "$_ID ASC"
            )
            val questions = ArrayList<Question>()
            while (cursor.moveToNext()) {
                questions.add(mapFromCursor(cursor))
            }
            return questions
        } finally {
            cursor?.close()
        }
    }

    /**
     * Change language of built-in questions
     */
    fun changeLanguage(context: Context) = DbHelper.getInstance(context).writableDatabase.use {
        changeLanguage(context, it, R.string.q_feelings)
        changeLanguage(context, it, R.string.q_gratitude)
        changeLanguage(context, it, R.string.q_do_body)
        changeLanguage(context, it, R.string.q_do_close)
        changeLanguage(context, it, R.string.q_do_others)
        changeLanguage(context, it, R.string.q_insincerity)
        changeLanguage(context, it, R.string.q_preach)
        changeLanguage(context, it, R.string.q_lie)
        changeLanguage(context, it, R.string.q_irresponsibility)
    }

    private fun changeLanguage(context: Context, db: SQLiteDatabase, code: Int) {
        QUESTION_CODE_MAP.get(code)?.let {
            val values = ContentValues().apply {
                put(COLUMN_TEXT, context.getString(it.textId))
                put(COLUMN_DESCRIPTION, context.getString(it.descriptionId))
            }
            db.update(QUESTION_TABLE, values, "$COLUMN_CODE = '${context.getString(code)}'", null)
        }
    }
}
