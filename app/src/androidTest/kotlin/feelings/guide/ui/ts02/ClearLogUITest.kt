package feelings.guide.ui.ts02

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class ClearLogUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

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
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())
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
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())
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
        onView(withId(R.id.logFullAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionList_builtIn() {
        // given
        answerBuiltInQuestion(
            R.string.q_text_do_others,
            "Test clear log from question list."
        )

        // when
        clearLogFromQuestionList(R.string.q_text_do_others)

        // then
        openLogByQuestion(R.string.q_text_do_others)
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionLog_builtIn() {
        // given
        answerBuiltInQuestion(
            R.string.q_text_do_body,
            "Test clear log from question log.",
        )

        // when
        openLogByQuestion(R.string.q_text_do_body)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFull_builtIn() {
        // given
        answerBuiltInQuestion(
            R.string.q_text_gratitude,
            "Test clear log full.",
        )

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.logFullAnswer)).check(doesNotExist())
    }

    @Test
    fun clearLogFromQuestionList_userQuestion() {
        // given
        val question = "Test clear log from question list for user question?"
        addUserQuestion(question)
        answerQuestion(question, "Test answer.")

        // when
        clearLogFromQuestionList(question)

        // then
        openLogByQuestion(question)
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }

    @Test
    fun clearLogFromQuestionLog_userQuestion() {
        // given
        val question = "Test clear log from question log for user question?"
        addUserQuestion(question)
        answerQuestion(question, "Test answer.")

        // when
        openLogByQuestion(question)
        clearLogFromQuestionLog()

        // then
        onView(withId(R.id.logByQuestionAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }

    @Test
    fun clearLogFull_userQuestion() {
        // given
        val question = "Test clear log full for user question?"
        addUserQuestion(question)
        answerQuestion(question, "Test answer.")

        // when
        openFullLog()
        clearLogFull()

        // then
        onView(withId(R.id.logFullAnswer)).check(doesNotExist())

        // clean up user question
        pressBack()
        deleteUserQuestion(question)
    }
}