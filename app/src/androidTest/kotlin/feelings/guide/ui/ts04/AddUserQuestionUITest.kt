package feelings.guide.ui.ts04

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.util.*
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddUserQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

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
    fun emptyText_isNotSaved() {
        // when
        val question = " "
        addUserQuestion(question)

        // then
        checkSnackbar(R.string.msg_question_text_empty)
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