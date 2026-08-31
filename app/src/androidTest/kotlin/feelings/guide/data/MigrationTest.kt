package feelings.guide.data

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Builds a legacy (pre-Room) v3 SQLite database by hand — using the exact DDL the old
 * SQLiteOpenHelper used — then runs the v3->v4 migration against it and verifies the data,
 * including a soft-deleted deprecated built-in question and its answer, survives.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dbName = "migration-test.db"
    private val dbFile: File get() = context.getDatabasePath(dbName)

    @After
    fun tearDown() {
        dbFile.delete()
    }

    private fun createLegacyV3Database(seed: (androidx.sqlite.db.SupportSQLiteDatabase) -> Unit) {
        val legacyHelper: SupportSQLiteOpenHelper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            """CREATE TABLE question (
                                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                code TEXT, text TEXT, description TEXT,
                                is_user INTEGER, is_deleted INTEGER, is_hidden INTEGER
                            )"""
                        )
                        db.execSQL(
                            """CREATE TABLE answer (
                                _id INTEGER PRIMARY KEY,
                                question_id INTEGER, date_time TEXT, answer TEXT
                            )"""
                        )
                    }

                    override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )
        legacyHelper.writableDatabase.use(seed)
        legacyHelper.close()
    }

    @Test
    fun migrate3To4_preservesExistingAndSoftDeletedRows() = runBlocking {
        dbFile.delete()

        createLegacyV3Database { db ->
            // A live built-in question.
            db.execSQL(
                "INSERT INTO question (_id, code, text, description, is_user, is_deleted, is_hidden) VALUES (1, 'q_feelings', 'What do I feel right now?', '', 0, 0, 0)"
            )
            // A soft-deleted, deprecated built-in question whose answer must still show in the full log.
            db.execSQL(
                "INSERT INTO question (_id, code, text, description, is_user, is_deleted, is_hidden) VALUES (6, 'q_insincerity', 'With whom I was not myself?', '', 0, 1, 0)"
            )
            // A user question.
            db.execSQL(
                "INSERT INTO question (_id, code, text, description, is_user, is_deleted, is_hidden) VALUES (10, NULL, 'What did I read today?', NULL, 1, 0, 0)"
            )
            db.execSQL("INSERT INTO answer (_id, question_id, date_time, answer) VALUES (1, 1, '2020-11-14 21:45:16.000', 'Hope, Peace')")
            db.execSQL("INSERT INTO answer (_id, question_id, date_time, answer) VALUES (2, 6, '2020-11-10 09:00:00.000', 'Old deprecated answer')")
        }

        val db = Room.databaseBuilder(context, FeelingsDatabase::class.java, dbName)
            .addMigrations(migration3To4(context))
            .build()

        val questions = db.questionDao().getAllAsList()

        val feelings = questions.first { it.id == 1L }
        assertThat(feelings.code).isEqualTo("q_feelings")
        assertThat(feelings.isDeleted).isFalse()

        val deprecated = questions.first { it.id == 6L }
        assertThat(deprecated.code).isEqualTo("q_insincerity")
        assertThat(deprecated.isDeleted).isTrue()

        val userQuestion = questions.first { it.id == 10L }
        assertThat(userQuestion.isUser).isTrue()

        val answers = db.answerDao().getForExport(null)
        assertThat(answers).hasSize(2)
        assertThat(answers.map { it.questionId }).containsExactly(1L, 6L)

        db.close()
    }

    /**
     * A real pre-"mind"-feature v3 install already has all the other built-ins seeded by the
     * legacy app. The v3->v4 migration must backfill exactly the built-in the legacy app never
     * seeded (`q_do_mind`) without touching or duplicating anything else.
     */
    @Test
    fun migrate3To4_backfillsBuiltInQuestionsMissingFromLegacyInstall() = runBlocking {
        dbFile.delete()

        createLegacyV3Database { db ->
            for ((code, text) in listOf(
                "q_feelings" to "What do I feel right now?",
                "q_gratitude" to "Who can I thank?",
                "q_do_body" to "What can I do for my body?",
                "q_do_close" to "What can I do for my loved ones?",
                "q_do_others" to "What can I do for other people?",
            )) {
                db.execSQL(
                    "INSERT INTO question (code, text, description, is_user, is_deleted, is_hidden) VALUES (?, ?, '', 0, 0, 0)",
                    arrayOf<Any?>(code, text)
                )
            }
            db.execSQL("INSERT INTO question (code, text, description, is_user, is_deleted, is_hidden) VALUES (NULL, 'My own question', NULL, 1, 0, 0)")
        }

        val db = Room.databaseBuilder(context, FeelingsDatabase::class.java, dbName)
            .addMigrations(migration3To4(context))
            .build()

        val questions = db.questionDao().getAllAsList()
        assertThat(questions).hasSize(7)

        val mind = questions.singleOrNull { it.code == "q_do_mind" }
        assertThat(mind).isNotNull()
        assertThat(mind!!.text).isEqualTo(context.getString(feelings.guide.R.string.q_text_do_mind))

        // Pre-existing built-ins and the user question aren't duplicated or altered.
        assertThat(questions.count { it.code == "q_feelings" }).isEqualTo(1)
        assertThat(questions.count { it.isUser }).isEqualTo(1)

        db.close()
    }
}
