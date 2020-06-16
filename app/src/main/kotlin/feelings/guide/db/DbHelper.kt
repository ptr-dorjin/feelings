package feelings.guide.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import feelings.guide.R
import feelings.guide.answer.SQL_CREATE_ANSWER_TABLE
import feelings.guide.question.*
import feelings.guide.question.QuestionContract.QUESTION_CODE_MAP
import feelings.guide.util.SingletonHolder

private const val DATABASE_VERSION = 3
private const val DATABASE_NAME = "FeelingsGuide.db"

class DbHelper private constructor(private val context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_QUESTION_TABLE)
        db.execSQL(SQL_CREATE_ANSWER_TABLE)
        populateQuestions(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            UpgraderTo2(context).upgrade(db)
        }
        if (oldVersion < 3) {
            UpgraderTo3(context).upgrade(db)
        }
    }

    private fun populateQuestions(db: SQLiteDatabase) {
        populateQuestion(db, R.string.q_feelings)
        populateQuestion(db, R.string.q_gratitude)
        populateQuestion(db, R.string.q_do_body)
        populateQuestion(db, R.string.q_do_close)
        populateQuestion(db, R.string.q_do_others)
    }

    private fun populateQuestion(db: SQLiteDatabase, code: Int) {
        val values = ContentValues()

        val questionCode = QUESTION_CODE_MAP.get(code)
        values.put(COLUMN_CODE, context.getString(code))
        values.put(COLUMN_TEXT, context.getString(questionCode.textId))
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.descriptionId))
        values.put(COLUMN_IS_USER, false)
        values.put(COLUMN_IS_DELETED, false)
        values.put(COLUMN_IS_HIDDEN, false)

        db.insert(QUESTION_TABLE, null, values)
    }

    companion object : SingletonHolder<DbHelper, Context>(::DbHelper)
}
