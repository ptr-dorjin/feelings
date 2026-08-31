package feelings.guide.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class AnswerDaoTest {
    private lateinit var db: FeelingsDatabase
    private lateinit var answerDao: AnswerDao
    private lateinit var questionDao: QuestionDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FeelingsDatabase::class.java).build()
        answerDao = db.answerDao()
        questionDao = db.questionDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun deleteForDeletedOrHiddenQuestions_sweepsBoth() {
        runBlocking {
            val deletedQ = questionDao.insert(QuestionEntity(text = "Deleted", isUser = true))
            val hiddenQ = questionDao.insert(QuestionEntity(code = "q_x", text = "Hidden", isUser = false))
            val liveQ = questionDao.insert(QuestionEntity(code = "q_feelings", text = "Live", isUser = false))
            questionDao.softDeleteUserQuestion(deletedQ)
            questionDao.hideBuiltIn(hiddenQ)

            answerDao.insert(AnswerEntity(questionId = deletedQ, dateTime = LocalDateTime.now(), answerText = "a1"))
            answerDao.insert(AnswerEntity(questionId = hiddenQ, dateTime = LocalDateTime.now(), answerText = "a2"))
            answerDao.insert(AnswerEntity(questionId = liveQ, dateTime = LocalDateTime.now(), answerText = "a3"))

            answerDao.deleteForDeletedOrHiddenQuestions()

            val remaining = answerDao.observeAll().first()
            assertThat(remaining).hasSize(1)
            assertThat(remaining.single().questionId).isEqualTo(liveQ)
        }
    }

    @Test
    fun restoreAnswer_reinsertsWithSameId() {
        runBlocking {
            val q = questionDao.insert(QuestionEntity(code = "q_feelings", text = "Feelings", isUser = false))
            val id = answerDao.insert(AnswerEntity(questionId = q, dateTime = LocalDateTime.now(), answerText = "Hope"))
            val saved = answerDao.getById(id)!!

            answerDao.deleteById(id)
            assertThat(answerDao.getById(id)).isNull()

            answerDao.insert(saved)
            assertThat(answerDao.getById(id)?.answerText).isEqualTo("Hope")
        }
    }

    @Test
    fun observeStats_countsAndTracksLatest() {
        runBlocking {
            val q = questionDao.insert(QuestionEntity(code = "q_feelings", text = "Feelings", isUser = false))
            answerDao.insert(AnswerEntity(questionId = q, dateTime = LocalDateTime.of(2024, 1, 1, 10, 0), answerText = "old"))
            answerDao.insert(AnswerEntity(questionId = q, dateTime = LocalDateTime.of(2024, 1, 2, 10, 0), answerText = "new"))

            val stats = answerDao.observeStats().first()
            assertThat(stats).hasSize(1)
            assertThat(stats.single().count).isEqualTo(2)
            assertThat(stats.single().lastAnsweredAt).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0))
        }
    }
}
