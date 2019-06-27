package feelings.guide.ui.ts02

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.addAnswer
import feelings.guide.ui.addBuiltInAnswer
import feelings.guide.ui.addFeelingsAnswer
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.clearLogFromQuestionList
import feelings.guide.ui.clearLogFromQuestionLog
import feelings.guide.ui.clearLogFull
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.question.QuestionsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ClearLogUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun clearLogFromQuestionList_feelings() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        addFeelingsAnswer(R.string.anger, feeling)

        // when
        clearLogFromQuestionList(R.string.q_text_feelings)

        // then
        openLogByQuestion(R.string.q_text_feelings)
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionLog_feelings() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        addFeelingsAnswer(R.string.anger, feeling)

        // when
        openLogByQuestion(R.string.q_text_feelings)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFull_feelings() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        addFeelingsAnswer(R.string.anger, feeling)

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionList_builtIn() {
        // given
        addBuiltInAnswer(R.string.q_text_do_others, "UI tests")

        // when
        clearLogFromQuestionList(R.string.q_text_do_others)

        // then
        openLogByQuestion(R.string.q_text_do_others)
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionLog_builtIn() {
        // given
        addBuiltInAnswer(R.string.q_text_do_body, "Go to the gym")

        // when
        openLogByQuestion(R.string.q_text_do_body)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFull_builtIn() {
        // given
        addBuiltInAnswer(R.string.q_text_gratitude, "Mom")

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionList_userQuestion() {
        // given
        val question = "Windows or MacOS?"
        addUserQuestion(question)
        addAnswer(question, "Linux")

        // when
        clearLogFromQuestionList(question)

        // then
        openLogByQuestion(question)
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        Espresso.pressBack()
        deleteUserQuestion(question, false)
    }

    @Test
    fun clearLogFromQuestionLog_userQuestion() {
        // given
        val question = "Intel or AMD?"
        addUserQuestion(question)
        addAnswer(question, "ARM")

        // when
        openLogByQuestion(question)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        Espresso.pressBack()
        deleteUserQuestion(question, false)
    }

    @Test
    fun clearLogFull_userQuestion() {
        // given
        val question = "Windows or MacOS?"
        addUserQuestion(question)
        addAnswer(question, "Linux")

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        Espresso.pressBack()
        deleteUserQuestion(question, false)
    }
}