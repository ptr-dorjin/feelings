package feelings.guide.ui.ts01

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.randomAlphanumericString
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.addUserQuestion
import feelings.guide.ui.util.answerBuiltInQuestion
import feelings.guide.ui.util.answerFeelings
import feelings.guide.ui.util.answerQuestion
import feelings.guide.ui.util.checkLastAnswerInLogByQuestion
import feelings.guide.ui.util.checkLastAnswerInLogFull
import feelings.guide.ui.util.checkNoSnackbar
import feelings.guide.ui.util.checkSnackbar
import feelings.guide.ui.util.deleteUserQuestion
import feelings.guide.ui.util.openFullLog
import feelings.guide.ui.util.openLogByQuestion
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@LargeTest
class AddAnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun answerFeelings_addsLog() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        answerFeelings(R.string.anger, feeling)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(R.string.q_text_feelings)

        // then
        checkLastAnswerInLogByQuestion(feeling)

        // when
        pressBack()
        openFullLog()

        // then
        checkLastAnswerInLogFull(feeling)
    }

    @Test
    fun answerBuiltInQuestion_addsToLog() {
        // given
        val answer = "Test answer built-in question."
        answerBuiltInQuestion(R.string.q_text_do_others, answer)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(R.string.q_text_do_others)

        // then
        checkLastAnswerInLogByQuestion(answer)

        // when
        pressBack()
        openFullLog()

        // then
        checkLastAnswerInLogFull(answer)
    }

    @Test
    fun answerUserQuestion_addsToLog() {
        // given
        val question = "Test answer user question adds to log?"
        val answer = "Test answer user question."
        addUserQuestion(question)
        answerQuestion(question, answer)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(question)

        // then
        checkLastAnswerInLogByQuestion(answer)

        // when
        pressBack()
        openFullLog()

        // then
        checkLastAnswerInLogFull(answer)

        // clean up user question
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    fun pressUpOnAddAnswer_noSnackbarIsShown() {
        // given
        onView(withText(R.string.q_text_feelings)).perform(click())

        // todo fix navigation up
        // when
        //onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        checkNoSnackbar(R.string.msg_answer_added_success)
    }

    @Test
    fun pressBackOnAddAnswer_noSnackbarIsShown() {
        // given
        onView(withText(R.string.q_text_feelings)).perform(click())

        // when
        pressBack()

        // then
        checkNoSnackbar(R.string.msg_answer_added_success)
    }
}