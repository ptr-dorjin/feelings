package feelings.guide.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import feelings.guide.R
import feelings.guide.question.*
import feelings.guide.question.QuestionContract.QUESTION_CODE_MAP

internal class UpgraderTo3(private val context: Context) {

    fun upgrade(db: SQLiteDatabase) {
        addIsHiddenColumn(db)

        deleteQuestionByCode(db, R.string.q_insincerity)
        deleteQuestionByCode(db, R.string.q_preach)
        deleteQuestionByCode(db, R.string.q_lie)
        deleteQuestionByCode(db, R.string.q_irresponsibility)

        changeLanguage(context, db, R.string.q_feelings)
        changeLanguage(context, db, R.string.q_gratitude)
        changeLanguage(context, db, R.string.q_do_body)
        changeLanguage(context, db, R.string.q_do_close)
        changeLanguage(context, db, R.string.q_do_others)
        changeLanguage(context, db, R.string.q_insincerity)
        changeLanguage(context, db, R.string.q_preach)
        changeLanguage(context, db, R.string.q_lie)
        changeLanguage(context, db, R.string.q_irresponsibility)
    }

    private fun addIsHiddenColumn(db: SQLiteDatabase) {
        db.execSQL("alter table $QUESTION_TABLE add $COLUMN_IS_HIDDEN INTEGER default 0")

        val values = ContentValues()
        values.put(COLUMN_IS_HIDDEN, 0)
        db.update(QUESTION_TABLE, values, null, null)
    }

    private fun deleteQuestionByCode(db: SQLiteDatabase, code: Int) {
        val values = ContentValues()
        values.put(COLUMN_IS_DELETED, true)

        val selection = "$COLUMN_CODE = ?"
        val selectionArgs = arrayOf(context.getString(code))

        db.update(QUESTION_TABLE, values, selection, selectionArgs)
    }

    private fun changeLanguage(context: Context, db: SQLiteDatabase, code: Int) {
        val values = ContentValues()

        val questionCode = QUESTION_CODE_MAP.get(code)
        values.put(COLUMN_TEXT, context.getString(questionCode.textId))
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.descriptionId))

        val selection = "$COLUMN_CODE = ?"
        val selectionArgs = arrayOf(context.getString(code))

        db.update(QUESTION_TABLE, values, selection, selectionArgs)
    }
}
