package feelings.guide.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import feelings.guide.R
import feelings.guide.data.answer.Answer
import feelings.guide.data.answer.AnswerDao
import feelings.guide.data.question.FEELINGS_ID
import feelings.guide.data.question.Question
import feelings.guide.data.question.QuestionContract
import feelings.guide.data.question.QuestionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val DATABASE_VERSION = 4
private const val DATABASE_NAME = "FeelingsGuide.db"

@Database(entities = [Question::class, Answer::class], version = DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, scope).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
            )
                    .addCallback(DbCallBack(context.applicationContext, scope))
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
        }
    }

    private class DbCallBack(
            private val context: Context,
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            instance?.let { database ->
                scope.launch {
                    val questionDao = database.questionDao()
                    populateQuestions(context.applicationContext, questionDao)
                }
            }
        }

        private suspend fun populateQuestions(context: Context, questionDao: QuestionDao) {
            populateQuestion(context, questionDao, R.string.q_feelings)
            populateQuestion(context, questionDao, R.string.q_gratitude)
            populateQuestion(context, questionDao, R.string.q_do_body)
            populateQuestion(context, questionDao, R.string.q_do_close)
            populateQuestion(context, questionDao, R.string.q_do_others)
        }

        private suspend fun populateQuestion(context: Context, questionDao: QuestionDao, codeResId: Int) {
            val questionCode = QuestionContract.QUESTION_CODE_MAP.get(codeResId)
            val code = context.getString(codeResId)
            val text = context.getString(questionCode.textId)

            val questionId = when (codeResId) {
                R.string.q_feelings -> FEELINGS_ID //make sure q_feelings ID is always 1
                else -> 0 //for others it doesn't matter
            }
            questionDao.createQuestion(Question(questionId, code, text, false))
        }
    }
}
