package feelings.guide.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val DATABASE_NAME = "FeelingsGuide.db"

@Database(
    entities = [QuestionEntity::class, AnswerEntity::class],
    version = 4,
    // Schema export is disabled: Room 2.8.4's KSP schema-bundle (de)serialization crashes
    // (AbstractMethodError) against the kotlinx-serialization-core version Navigation-Compose's
    // type-safe args pull onto the KSP processor classpath in this toolchain. Migrations below
    // don't depend on the exported JSON at runtime — only MigrationTestHelper-based tests would.
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class FeelingsDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
}

/**
 * v3 -> v4: the legacy `question`/`answer` tables had no NOT NULL constraints and `answer._id`
 * had no AUTOINCREMENT keyword. SQLite can't alter column constraints in place, so both tables
 * are rebuilt with Room's expected schema and their data copied over untouched — including
 * soft-deleted rows for the deprecated built-in questions, whose answers must keep showing in
 * the full log. The legacy `description` column is dropped: it was never surfaced anywhere.
 * Finally, any [BUILT_IN_QUESTIONS] entry the legacy app never seeded (e.g. the "mind" question,
 * added after v3 shipped) is inserted so upgrading installs end up with the same built-ins as a
 * fresh one.
 */
fun migration3To4(context: Context): Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE question_new (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                code TEXT,
                text TEXT NOT NULL DEFAULT '',
                is_user INTEGER NOT NULL DEFAULT 0,
                is_deleted INTEGER NOT NULL DEFAULT 0,
                is_hidden INTEGER NOT NULL DEFAULT 0
            )"""
        )
        db.execSQL(
            """INSERT INTO question_new (_id, code, text, is_user, is_deleted, is_hidden)
               SELECT _id, code, COALESCE(text, ''),
                      COALESCE(is_user, 0), COALESCE(is_deleted, 0), COALESCE(is_hidden, 0)
               FROM question"""
        )
        db.execSQL("DROP TABLE question")
        db.execSQL("ALTER TABLE question_new RENAME TO question")

        db.execSQL(
            """CREATE TABLE answer_new (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                question_id INTEGER NOT NULL,
                date_time TEXT NOT NULL,
                answer TEXT
            )"""
        )
        db.execSQL(
            """INSERT INTO answer_new (_id, question_id, date_time, answer)
               SELECT _id, question_id, date_time, answer FROM answer"""
        )
        db.execSQL("DROP TABLE answer")
        db.execSQL("ALTER TABLE answer_new RENAME TO answer")

        db.execSQL("CREATE INDEX IF NOT EXISTS index_answer_question_id ON answer(question_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_answer_date_time ON answer(date_time)")

        insertMissingBuiltInQuestions(db, context)
    }
}

fun seedBuiltInQuestions(db: SupportSQLiteDatabase, context: Context) {
    for (q in BUILT_IN_QUESTIONS) {
        db.execSQL(
            "INSERT INTO question (code, text, is_user, is_deleted, is_hidden) VALUES (?, ?, 0, 0, 0)",
            arrayOf<Any?>(q.code, context.getString(q.textResId))
        )
    }
}

/** Inserts any [BUILT_IN_QUESTIONS] entry whose `code` isn't already present in `question`. */
private fun insertMissingBuiltInQuestions(db: SupportSQLiteDatabase, context: Context) {
    val existingCodes = mutableSetOf<String>()
    db.query("SELECT code FROM question WHERE code IS NOT NULL").use { cursor ->
        while (cursor.moveToNext()) {
            existingCodes.add(cursor.getString(0))
        }
    }
    for (q in BUILT_IN_QUESTIONS) {
        if (q.code in existingCodes) continue
        db.execSQL(
            "INSERT INTO question (code, text, is_user, is_deleted, is_hidden) VALUES (?, ?, 0, 0, 0)",
            arrayOf<Any?>(q.code, context.getString(q.textResId))
        )
    }
}

class SeedCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedBuiltInQuestions(db, context)
    }
}
