package feelings.guide.ui.ts07

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.randomAlphanumericString
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.util.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@LargeTest
class EditAnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

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
        openEditAnswer(answer)

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
        openEditAnswer(answer)

        // then
        onView(withId(R.id.answerText)).check(matches(withText(answer)))
        onView(first(withId(R.id.labelFeelingsGroup))).check(matches(isDisplayed()))
    }

    @Test
    fun openOnEditAnswerForUserQuestion_fullLog() {
        // given
        val question = "Quartz or smart watch?"
        val answer = "Quartz"
        addUserQuestion(question)
        answerQuestion(question, answer)

        // when
        openFullLog()
        openEditAnswer(answer)

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
        val question = "Metal or leather watch band?"
        val answer = "Leather"
        addUserQuestion(question)
        answerQuestion(question, answer)

        // when
        openLogByQuestion(question)
        openEditAnswer(answer)

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
        editAnswer(answer, new)

        // then
        onView(withText(new)).check(matches(isDisplayed()))
    }

    @Test
    fun saveEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        val new = randomAlphanumericString()

        // when
        openLogByQuestion(R.string.q_text_feelings)
        editAnswer(answer, new)

        // then
        onView(withText(new)).check(matches(isDisplayed()))
    }

    @Test
    fun saveEditAnswerForUserQuestion_fullLog() {
        // given
        val question = "Black or brown?"
        val answer = "black"
        addUserQuestion(question)
        answerQuestion(question, answer)
        val new = "brown"

        // when
        openFullLog()
        editAnswer(answer, new)

        // then
        onView(withText(new)).check(matches(isDisplayed()))

        // clean up
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    fun saveEditAnswerForUserQuestion_questionLog() {
        // given
        val question = "Roman or Arabic digits?"
        val answer = "Roman"
        addUserQuestion(question)
        answerQuestion(question, answer)
        val new = "binary"

        // when
        openLogByQuestion(question)
        editAnswer(answer, new)

        // then
        onView(withText(new)).check(matches(isDisplayed()))

        // clean up
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    @Ignore("Can be enabled after adding NavigationGraph")
    fun pressUpOnEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()
        openEditAnswer(answer)

        // when
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Can be enabled after adding NavigationGraph")
    fun pressUpOnEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        openEditAnswer(answer)

        // when
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Can be enabled after adding NavigationGraph")
    fun pressBackOnEditAnswerForFeelings_fullLog() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()
        openEditAnswer(answer)

        // when
        pressBack()

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Can be enabled after adding NavigationGraph")
    fun pressBackOnEditAnswerForFeelings_questionLog() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        openEditAnswer(answer)

        // when
        pressBack()

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }

}