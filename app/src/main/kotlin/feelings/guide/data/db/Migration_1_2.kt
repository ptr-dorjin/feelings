package feelings.guide.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.provider.BaseColumns._ID
import androidx.sqlite.db.SupportSQLiteDatabase
import feelings.guide.R
import feelings.guide.data.question.COLUMN_CODE
import feelings.guide.data.question.COLUMN_DESCRIPTION
import feelings.guide.data.question.COLUMN_TEXT
import feelings.guide.data.question.QUESTION_TABLE
import feelings.guide.data.question.QuestionContract.QUESTION_CODE_MAP

internal val MIGRATION_1_2 = object : ContextAwareMigration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("alter table question add code TEXT")
        db.execSQL("alter table question add description TEXT")
        updateQuestionById(db, 1, R.string.q_feelings)
        updateQuestionById(db, 2, R.string.q_insincerity)
        updateQuestionById(db, 3, R.string.q_gratitude)
        updateQuestionById(db, 4, R.string.q_preach)
        updateQuestionById(db, 5, R.string.q_lie)
        updateQuestionById(db, 6, R.string.q_irresponsibility)
        updateQuestionById(db, 7, R.string.q_do_body)
        updateQuestionById(db, 8, R.string.q_do_close)
        updateQuestionById(db, 9, R.string.q_do_others)
    }

    private fun updateQuestionById(db: SupportSQLiteDatabase, questionId: Int, code: Int) {
        val values = ContentValues()

        val questionCode = QUESTION_CODE_MAP.get(code)
        values.put(COLUMN_CODE, context.getString(code))
        values.put(COLUMN_TEXT, context.getString(questionCode.textId))
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.descriptionId))

        val selection = "$_ID = ?"
        val selectionArgs = arrayOf(questionId.toString())

        db.update(QUESTION_TABLE, CONFLICT_REPLACE, values, selection, selectionArgs)
    }
}
