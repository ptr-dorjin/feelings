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

@RunWith(AndroidJUnit4::class)
class QuestionDaoTest {
    private lateinit var db: FeelingsDatabase
    private lateinit var dao: QuestionDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FeelingsDatabase::class.java).build()
        dao = db.questionDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun visibleQuestions_excludeDeletedAndHidden() {
        // Kotlin note: `= runBlocking { ... }` expression-body infers the test method's return
        // type from the last statement — `containsExactly(...)` returns Truth's `Ordered` (a
        // Java, non-void type), which fails JUnit4's "test method should be void" validation for
        // the whole class. Block-body keeps the inferred return type Unit regardless.
        runBlocking {
            dao.insert(QuestionEntity(code = "q_feelings", text = "Feelings", isUser = false))
            val deletedId = dao.insert(QuestionEntity(text = "Deleted", isUser = true))
            val hiddenId = dao.insert(QuestionEntity(code = "q_x", text = "Hidden", isUser = false))
            dao.softDeleteUserQuestion(deletedId)
            dao.hideBuiltIn(hiddenId)

            val visible = dao.observeVisible().first()
            assertThat(visible.map { it.text }).containsExactly("Feelings")
        }
    }

    @Test
    fun deleteQuestion_onlyAffectsUserQuestions() {
        runBlocking {
            val builtInId = dao.insert(QuestionEntity(code = "q_feelings", text = "Feelings", isUser = false))
            dao.softDeleteUserQuestion(builtInId)
            val builtIn = dao.getById(builtInId)!!
            assertThat(builtIn.isDeleted).isFalse()
        }
    }

    @Test
    fun restoreHidden_unhidesOnlyBuiltIns() {
        runBlocking {
            val id = dao.insert(QuestionEntity(code = "q_x", text = "Hidden", isUser = false))
            dao.hideBuiltIn(id)
            dao.restoreHidden()
            assertThat(dao.getById(id)!!.isHidden).isFalse()
        }
    }

    @Test
    fun visibleQuestions_orderBuiltInsExplicitlyThenUserQuestionsByCreationOrder() {
        runBlocking {
            // Inserted out of the intended display order, and with a user question sorting
            // between built-ins by `_id` — simulates an upgrading install where new built-ins
            // are appended after existing user questions.
            dao.insert(QuestionEntity(code = "q_do_others", text = "Others", isUser = false))
            dao.insert(QuestionEntity(code = "q_do_close", text = "Close", isUser = false))
            dao.insert(QuestionEntity(text = "User question", isUser = true))
            dao.insert(QuestionEntity(code = "q_feelings", text = "Feelings", isUser = false))
            dao.insert(QuestionEntity(code = "q_do_mind", text = "Mind", isUser = false))
            dao.insert(QuestionEntity(code = "q_gratitude", text = "Gratitude", isUser = false))
            dao.insert(QuestionEntity(code = "q_do_body", text = "Body", isUser = false))
            dao.insert(QuestionEntity(text = "Later user question", isUser = true))

            val visible = dao.observeVisible().first()
            assertThat(visible.map { it.text }).containsExactly(
                "Feelings", "Gratitude", "Body", "Mind", "Close", "Others",
                "User question", "Later user question",
            ).inOrder()
        }
    }
}
