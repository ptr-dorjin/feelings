package feelings.guide.data.answer

import android.content.Context
import android.database.Cursor
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.common.truth.Truth.assertThat
import feelings.guide.data.question.Question
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.util.*

private const val ID: Long = 1
private const val QUESTION_ID: Long = 1
private const val QUESTION_ID_2: Long = 2
private const val ANSWER_TEXT = "test answer"

class AnswerStoreTest {
    private val now = LocalDateTime.now()
    private lateinit var context: Context

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @After
    fun after() {
        AnswerStore.deleteAll(context)
    }

    /**
     * for performance tests only
     */
    //@Test
    fun createForPerformanceTest() {
        for (i in 0..10000) {
            val created = AnswerStore.saveAnswer(context, Answer(QUESTION_ID, now.minusSeconds(i.toLong()), ANSWER_TEXT + i))
            assertThat(created).isTrue()
        }

    }

    @Test
    fun testCreate() {
        // given
        val answer = Answer(QUESTION_ID, now, ANSWER_TEXT)

        // when
        val created = AnswerStore.saveAnswer(context, answer)

        // then
        assertThat(created).isTrue()
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer)
    }

    @Test
    fun testCreateWithId() {
        // given
        val answer = Answer(ID, QUESTION_ID, now, ANSWER_TEXT)

        // when
        val created = AnswerStore.saveAnswer(context, answer)

        // then
        assertThat(created).isTrue()
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer)
    }

    @Test
    fun testEdit() {
        // given
        val answer = Answer(QUESTION_ID, now, ANSWER_TEXT)
        AnswerStore.saveAnswer(context, answer)
        answer.answerText = "updated text"

        // when
        val updated = AnswerStore.updateAnswer(context, answer)

        // then
        assertThat(updated).isTrue()
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer)
    }

    @Test
    fun testGetAll() {
        // given
        val answer1 = Answer(QUESTION_ID, now.minusMinutes(3), ANSWER_TEXT)
        val answer2 = Answer(QUESTION_ID_2, now.minusMinutes(1), "another one")
        val answer3 = Answer(3, now.minusMinutes(2), "one more")
        AnswerStore.saveAnswer(context, answer1)
        AnswerStore.saveAnswer(context, answer2)
        AnswerStore.saveAnswer(context, answer3)

        // when
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))

        // then
        assertThat(answers)
            .containsExactly(answer2, answer3, answer1)
            .inOrder()
    }

    @Test
    fun testGetByQuestionId() {
        // given
        val answer1 = Answer(QUESTION_ID, now.minusMinutes(3), ANSWER_TEXT)
        val answer2 = Answer(QUESTION_ID_2, now.minusMinutes(1), "another one")
        val answer3 = Answer(QUESTION_ID, now.minusMinutes(2), "one more")
        AnswerStore.saveAnswer(context, answer1)
        AnswerStore.saveAnswer(context, answer2)
        AnswerStore.saveAnswer(context, answer3)

        // when
        val answers = cursorToAnswers(AnswerStore.getAnswers(context, QUESTION_ID))

        // then
        assertThat(answers)
            .containsExactly(answer3, answer1)
            .inOrder()
    }

    @Test
    fun testDeleteById() {
        // given
        val answer1 = Answer(QUESTION_ID, now.minusMinutes(3), ANSWER_TEXT)
        val answer2 = Answer(QUESTION_ID_2, now.minusMinutes(1), "another one")
        AnswerStore.saveAnswer(context, answer1)
        AnswerStore.saveAnswer(context, answer2)

        // when
        AnswerStore.deleteById(context, answer1.id)

        // then
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer2)
    }

    @Test
    fun testDeleteByIdAndThenUndo() {
        // given
        val answer = Answer(QUESTION_ID, now, ANSWER_TEXT)
        AnswerStore.saveAnswer(context, answer)
        AnswerStore.deleteById(context, answer.id)

        // when
        AnswerStore.saveAnswer(context, answer)

        // then
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer)
    }

    @Test
    fun testDeleteByQuestionId() {
        // given
        val answer = Answer(QUESTION_ID_2, now.minusMinutes(1), "another one")
        AnswerStore.saveAnswer(context, Answer(QUESTION_ID, now.minusMinutes(3), ANSWER_TEXT))
        AnswerStore.saveAnswer(context, answer)
        AnswerStore.saveAnswer(context, Answer(QUESTION_ID, now.minusMinutes(2), "one more"))

        // when
        AnswerStore.deleteByQuestionId(context, QUESTION_ID)

        // then
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer)
    }

    @Test
    fun testDeleteForDeletedQuestions() {
        // given
        val questionId = QuestionService.createQuestion(context, Question("test question"))
        val answer = Answer(questionId, now.minusMinutes(3), ANSWER_TEXT)
        val answer2 = Answer(QUESTION_ID, now.minusMinutes(2), "one more")
        AnswerStore.saveAnswer(context, answer)
        AnswerStore.saveAnswer(context, answer2)

        // when
        QuestionService.deleteQuestion(context, questionId)
        AnswerStore.deleteForDeletedQuestions(context)

        // then
        val answers = cursorToAnswers(AnswerStore.getAnswers(context))
        assertThat(answers)
            .containsExactly(answer2)
    }

    private fun cursorToAnswers(cursor: Cursor): List<Answer> {
        assertThat(cursor).isNotNull()
        cursor.use {
            val list = ArrayList<Answer>()
            while (it.moveToNext()) {
                list.add(AnswerStore.mapFromCursor(it))
            }
            return list
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            AnswerStore.deleteAll(getApplicationContext())
        }
    }
}
