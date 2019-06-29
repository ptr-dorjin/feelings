package feelings.guide.ui.ts02

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.*
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
        answerFeelings(R.string.anger, feeling)

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
        answerFeelings(R.string.anger, feeling)

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
        answerFeelings(R.string.anger, feeling)

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionList_builtIn() {
        // given
        answerBuiltInQuestion(R.string.q_text_do_others, "UI tests")

        // when
        clearLogFromQuestionList(R.string.q_text_do_others)

        // then
        openLogByQuestion(R.string.q_text_do_others)
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionLog_builtIn() {
        // given
        answerBuiltInQuestion(R.string.q_text_do_body, "Go to the gym")

        // when
        openLogByQuestion(R.string.q_text_do_body)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFull_builtIn() {
        // given
        answerBuiltInQuestion(R.string.q_text_gratitude, "Mom")

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
        answerQuestion(question, "Linux")

        // when
        clearLogFromQuestionList(question)

        // then
        openLogByQuestion(question)
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }

    @Test
    fun clearLogFromQuestionLog_userQuestion() {
        // given
        val question = "Intel or AMD?"
        addUserQuestion(question)
        answerQuestion(question, "ARM")

        // when
        openLogByQuestion(question)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }

    @Test
    fun clearLogFull_userQuestion() {
        // given
        val question = "Fender or Gibson?"
        addUserQuestion(question)
        answerQuestion(question, "Epiphone")

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.answerLogAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }
}