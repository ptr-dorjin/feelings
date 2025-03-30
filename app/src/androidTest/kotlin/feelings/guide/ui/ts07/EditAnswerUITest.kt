package feelings.guide.ui.ts07

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.randomAlphanumericString
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class EditAnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun openOnEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()

        // when
        openFullLog()
        openEditAnswerInLogFull(answer)

        // then
        onView(withId(R.id.answerText)).check(matches(withText(answer)))
        onView(first(withId(R.id.labelFeelingsGroup))).check(matches(isDisplayed()))
    }

    @Test
    fun openOnEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()

        // when
        openLogByQuestion(R.string.q_text_feelings)
        openEditAnswerInLogByQuestion(answer)

        // then
        onView(withId(R.id.answerText)).check(matches(withText(answer)))
        onView(first(withId(R.id.labelFeelingsGroup))).check(matches(isDisplayed()))
    }

    @Test
    fun openOnEditAnswerForUserQuestion_fullLog() {
        // given
        val question = "Test open on edit answer for user question - full log?"
        val answer = "Test open on edit answer for user question - full log."
        addUserQuestion(question)
        answerQuestion(question, answer)

        // when
        openFullLog()
        openEditAnswerInLogFull(answer)

        // then
        onView(withId(R.id.answerText)).check(matches(withText(answer)))

        // clean up
        pressBack()
        pressBack() //should be only one back. remove after NavGraph is added
        deleteUserQuestion(question, true)
    }

    @Test
    fun openOnEditAnswerForUserQuestion_questionLog() {
        // given
        val question = "Test open on edit answer for user question - question log?"
        val answer = "Test open on edit answer for user question - question log."
        addUserQuestion(question)
        answerQuestion(question, answer)

        // when
        openLogByQuestion(question)
        openEditAnswerInLogByQuestion(answer)

        // then
        onView(withId(R.id.answerText)).check(matches(withText(answer)))

        // clean up
        pressBack()
        pressBack() //should be only one back. remove after NavGraph is added
        deleteUserQuestion(question, true)
    }

    @Test
    fun saveEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()
        val new = randomAlphanumericString()

        // when
        openFullLog()
        editAnswerInLogFull(answer, new)

        // then
        checkSnackbar(R.string.msg_answer_updated_success)
        onView(withText(new)).check(matches(isDisplayed()))
    }

    @Test
    fun saveEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        val new = randomAlphanumericString()

        // when
        openLogByQuestion(R.string.q_text_feelings)
        editAnswerInLogByQuestion(answer, new)

        // then
        checkSnackbar(R.string.msg_answer_updated_success)
        onView(withText(new)).check(matches(isDisplayed()))
    }

    @Test
    fun saveEditAnswerForUserQuestion_fullLog() {
        // given
        val question = "Test save edit answer for user question - full log?"
        val answer = "Test save edit answer for user question - full log."
        addUserQuestion(question)
        answerQuestion(question, answer)
        val new = "Test save edit answer for user question - full log - updated."

        // when
        openFullLog()
        editAnswerInLogFull(answer, new)

        // then
        checkSnackbar(R.string.msg_answer_updated_success)
        onView(withText(new)).check(matches(isDisplayed()))

        // clean up
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    fun saveEditAnswerForUserQuestion_questionLog() {
        // given
        val question = "Test save edit answer for user question - question log?"
        val answer = "Test save edit answer for user question - question log."
        addUserQuestion(question)
        answerQuestion(question, answer)
        val new = "Test save edit answer for user question - question log - updated."

        // when
        openLogByQuestion(question)
        editAnswerInLogByQuestion(answer, new)

        // then
        checkSnackbar(R.string.msg_answer_updated_success)
        onView(withText(new)).check(matches(isDisplayed()))

        // clean up
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    fun pressUpOnEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()
        openEditAnswerInLogFull(answer)

        // todo fix navigation up
        // when
        //onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
        checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressUpOnEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        openEditAnswerInLogByQuestion(answer)

        // todo fix navigation up
        // when
        //onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
        checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressBackOnEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()
        openEditAnswerInLogFull(answer)

        // when
        pressBack()

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
        checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressBackOnEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        openEditAnswerInLogByQuestion(answer)

        // when
        pressBack()

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
        checkNoSnackbar(R.string.msg_answer_updated_success)
    }

}