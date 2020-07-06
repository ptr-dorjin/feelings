package feelings.guide.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.sqlite.db.SupportSQLiteDatabase
import feelings.guide.R
import feelings.guide.data.question.*
import feelings.guide.data.question.QuestionContract.QUESTION_CODE_MAP

internal val MIGRATION_2_3 = object : ContextAwareMigration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {
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

    private fun addIsHiddenColumn(db: SupportSQLiteDatabase) {
        db.execSQL("alter table $QUESTION_TABLE add $COLUMN_IS_HIDDEN INTEGER default 0")

        val values = ContentValues()
        values.put(COLUMN_IS_HIDDEN, 0)
        db.update(QUESTION_TABLE, CONFLICT_REPLACE, values, null, null)
    }

    private fun deleteQuestionByCode(db: SupportSQLiteDatabase, code: Int) {
        val values = ContentValues()
        values.put(COLUMN_IS_DELETED, true)

        val selection = "$COLUMN_CODE = ?"
        val selectionArgs = arrayOf(context.getString(code))

        db.update(QUESTION_TABLE, CONFLICT_REPLACE, values, selection, selectionArgs)
    }

    private fun changeLanguage(context: Context, db: SupportSQLiteDatabase, code: Int) {
        val values = ContentValues()

        val questionCode = QUESTION_CODE_MAP.get(code)
        values.put(COLUMN_TEXT, context.getString(questionCode.textId))
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.descriptionId))

        val selection = "$COLUMN_CODE = ?"
        val selectionArgs = arrayOf(context.getString(code))

        db.update(QUESTION_TABLE, CONFLICT_REPLACE, values, selection, selectionArgs)
    }
}
