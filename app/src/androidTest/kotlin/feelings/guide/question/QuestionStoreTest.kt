package feelings.guide.question

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import feelings.guide.db.DbHelper
import feelings.guide.question.QuestionContract.*
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class QuestionStoreTest {
    private val toBeOrNotToBe = "To be or not to be?"
    private val wouldYouLikeToUnderstandNothing = "Would you like to understand nothing?"
    private val someBuiltInQuestion = "Some built-in question"
    private val testBuiltInQuestionCode = "test_code"

    private lateinit var context: Context

    private var questionId: Long = 0
    private var questionId2: Long = 0
    private var questionId3: Long = 0

    @Before
    fun before() {
        questionId = 0
        questionId2 = 0
        questionId3 = 0
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun after() {
        if (questionId != 0L) QuestionStore.deleteQuestion(context, questionId)
        if (questionId2 != 0L) QuestionStore.deleteQuestion(context, questionId2)
        if (questionId3 != 0L) QuestionStore.deleteQuestion(context, questionId3)

        deleteBuiltInQuestion()
    }

    @Test
    fun shouldGetById() {
        // given
        questionId = QuestionStore.createQuestion(context, Question(toBeOrNotToBe))

        // when
        val fromDB = QuestionStore.getById(context, questionId)
        val fromDB2 = QuestionStore.getById(context, questionId + 1)

        // then
        assertThat(fromDB).isNotNull()
        assertThat(fromDB2).isNull()
    }

    @Test
    fun shouldCreate() {
        // when
        questionId = QuestionStore.createQuestion(context, Question(toBeOrNotToBe))
        questionId2 = QuestionStore.createQuestion(context, Question(wouldYouLikeToUnderstandNothing))

        // then
        assertThat(questionId).isNotEqualTo(-1)
        assertThat(questionId2).isNotEqualTo(-1)
        assertThat(questionId).isNotEqualTo(questionId2)
    }

    @Test
    fun shouldUpdate() {
        // given
        val question = Question(toBeOrNotToBe)
        questionId = QuestionStore.createQuestion(context, question)
        question.id = questionId
        question.text = wouldYouLikeToUnderstandNothing

        // when
        val updated = QuestionStore.updateQuestion(context, question)

        // then
        assertThat(updated).isTrue()
        val fromDB = QuestionStore.getById(context, questionId)
        assertThat(fromDB).isNotNull()
        assertThat(fromDB!!.text).isEqualTo(wouldYouLikeToUnderstandNothing)
    }

    @Test
    fun shouldDelete() {
        // given
        questionId = QuestionStore.createQuestion(context, Question(toBeOrNotToBe))

        // when
        val deleted = QuestionStore.deleteQuestion(context, questionId)

        // then
        assertThat(deleted).isTrue()
        val fromDB = QuestionStore.getById(context, questionId)
        assertThat(fromDB).isNotNull()
        assertThat(fromDB!!.isDeleted).isTrue()
    }

    @Test
    fun shouldHideBuiltInQuestion() {
        // given
        val builtInQuestion = Question()
        builtInQuestion.code = testBuiltInQuestionCode
        builtInQuestion.text = someBuiltInQuestion
        questionId = createBuiltInQuestion(builtInQuestion)

        // when
        val hidden = QuestionStore.hideQuestion(context, questionId)

        // then
        assertThat(hidden).isTrue()
        val fromDB = QuestionStore.getById(context, questionId)
        assertThat(fromDB).isNotNull()
        assertThat(fromDB!!.isHidden).isTrue()
    }

    @Test
    fun shouldGetAll() {
        // given
        questionId = QuestionStore.createQuestion(context, Question(toBeOrNotToBe))

        questionId2 = QuestionStore.createQuestion(context, Question(wouldYouLikeToUnderstandNothing))
        QuestionStore.deleteQuestion(context, questionId2)

        val builtInQuestion = Question()
        builtInQuestion.code = testBuiltInQuestionCode
        builtInQuestion.text = someBuiltInQuestion
        questionId3 = createBuiltInQuestion(builtInQuestion)
        QuestionStore.hideQuestion(context, questionId3)

        // when
        val cursor = QuestionStore.getAll(context)

        // then
        val questions = cursorToQuestions(cursor)
        assertThat(questions.size).isGreaterThan(0) //also contains real app's questions

        var containsQuestion1 = false
        for (question in questions) {
            if (question.id == questionId) {
                containsQuestion1 = true
            }
            assertThat(question.id).isNotEqualTo(questionId2)
            assertThat(question.id).isNotEqualTo(questionId3)
        }
        assertThat(containsQuestion1).isTrue()
    }

    private fun cursorToQuestions(cursor: Cursor): List<Question> {
        assertThat(cursor).isNotNull()
        cursor.use {
            val list = ArrayList<Question>()
            while (it.moveToNext()) {
                list.add(QuestionStore.mapFromCursor(it))
            }
            return list
        }
    }

    private fun createBuiltInQuestion(question: Question): Long {
        DbHelper.getInstance(context).writableDatabase.use { db ->
            val values = ContentValues()
            values.put(COLUMN_TEXT, question.text)
            values.put(COLUMN_CODE, question.code)
            values.put(COLUMN_DESCRIPTION, question.description)
            values.put(COLUMN_IS_USER, false)
            values.put(COLUMN_IS_DELETED, false)
            values.put(COLUMN_IS_HIDDEN, false)

            return db.insert(QUESTION_TABLE, null, values)
        }
    }

    private fun deleteBuiltInQuestion() {
        DbHelper.getInstance(context).writableDatabase.use { db ->
            val selection = "$COLUMN_CODE = ?"
            val selectionArgs = arrayOf(testBuiltInQuestionCode)
            db.delete(QUESTION_TABLE, selection, selectionArgs)
        }
    }
}
