package feelings.guide.question

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.common.truth.Truth.assertThat
import feelings.guide.db.DbHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

private const val TO_BE_OR_NOT_TO_BE = "To be or not to be?"
private const val WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING = "Would you like to understand nothing?"
private const val SOME_BUILT_IN_QUESTION = "Some built-in question"
private const val TEST_BUILT_IN_QUESTION_CODE = "test_code"

class QuestionStoreTest {
    private lateinit var context: Context

    private var questionId: Long = 0
    private var questionId2: Long = 0
    private var questionId3: Long = 0

    @Before
    fun before() {
        questionId = 0
        questionId2 = 0
        questionId3 = 0
        context = getApplicationContext()
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
        questionId = QuestionStore.createQuestion(context, Question(TO_BE_OR_NOT_TO_BE))

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
        questionId = QuestionStore.createQuestion(context, Question(TO_BE_OR_NOT_TO_BE))
        questionId2 = QuestionStore.createQuestion(context, Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING))

        // then
        assertThat(questionId).isNotEqualTo(-1)
        assertThat(questionId2).isNotEqualTo(-1)
        assertThat(questionId).isNotEqualTo(questionId2)
    }

    @Test
    fun shouldUpdate() {
        // given
        val question = Question(TO_BE_OR_NOT_TO_BE)
        questionId = QuestionStore.createQuestion(context, question)
        question.id = questionId
        question.text = WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING

        // when
        val updated = QuestionStore.updateQuestion(context, question)

        // then
        assertThat(updated).isTrue()
        val fromDB = QuestionStore.getById(context, questionId)
        assertThat(fromDB).isNotNull()
        assertThat(fromDB!!.text).isEqualTo(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING)
    }

    @Test
    fun shouldDelete() {
        // given
        questionId = QuestionStore.createQuestion(context, Question(TO_BE_OR_NOT_TO_BE))

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
        builtInQuestion.code = TEST_BUILT_IN_QUESTION_CODE
        builtInQuestion.text = SOME_BUILT_IN_QUESTION
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
        questionId = QuestionStore.createQuestion(context, Question(TO_BE_OR_NOT_TO_BE))

        questionId2 = QuestionStore.createQuestion(context, Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING))
        QuestionStore.deleteQuestion(context, questionId2)

        val builtInQuestion = Question()
        builtInQuestion.code = TEST_BUILT_IN_QUESTION_CODE
        builtInQuestion.text = SOME_BUILT_IN_QUESTION
        questionId3 = createBuiltInQuestion(builtInQuestion)
        QuestionStore.hideQuestion(context, questionId3)

        // when
        val cursor = QuestionStore.getAllVisible(context)

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
            val selectionArgs = arrayOf(TEST_BUILT_IN_QUESTION_CODE)
            db.delete(QUESTION_TABLE, selection, selectionArgs)
        }
    }
}
