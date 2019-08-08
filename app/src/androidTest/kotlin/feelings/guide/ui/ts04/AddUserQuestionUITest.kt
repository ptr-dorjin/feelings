package feelings.guide.ui.ts04

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
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class AddUserQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun addUserQuestion_appearsInList() {
        // given
        val question = "Minor or major scale?"

        // when
        addUserQuestion(question)

        // then
        checkQuestion(question)

        // clean up the question
        deleteUserQuestion(question)
    }

    @Test
    fun emptyText_saveBtnIsDisabled() {
        // when
        onView(withId(R.id.questionFab)).perform(click())

        // then
        onView(withText(R.string.btn_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun blankText_saveBtnIsDisabled() {
        // when
        val question = "   "
        addUserQuestion(question)

        // then
        onView(withText(R.string.btn_save)).check(matches(not(isEnabled())))
        onView(withText(R.string.btn_cancel)).perform(click())
        checkNoQuestion(question)
    }

    @Test
    fun tapOutsideAddQuestionDialog_popupIsNotClosed() {
        // given
        onView(withId(R.id.questionFab)).perform(click())

        // when
        onView(withId(R.id.questionTextEdit)).perform(clickXY(0, -500))

        // then
        onView(withId(R.id.questionTextEdit)).check(matches(isDisplayed()))
    }

    @Test
    fun pressBackOnAddQuestionDialog_popupIsNotClosed() {
        // given
        onView(withId(R.id.questionFab)).perform(click())

        // when
        pressBack()

        // then
        onView(withId(R.id.questionTextEdit)).check(matches(isDisplayed()))
    }
}