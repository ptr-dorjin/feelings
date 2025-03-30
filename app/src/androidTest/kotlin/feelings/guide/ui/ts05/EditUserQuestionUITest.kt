package feelings.guide.ui.ts05

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class EditUserQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun editQuestion_isUpdatedOnQuestionList() {
        // given
        val old = "Test edit question is updated on question list - old?"
        addUserQuestion(old)

        // when
        val updated = "Test edit question is updated on question list - updated?"
        editUserQuestion(old, updated)

        // then
        checkQuestion(updated)

        // clean up
        deleteUserQuestion(updated)
    }

    @Test
    fun editQuestion_isUpdatedOnAnswerDialog() {
        // given
        val old = "Test edit question is updated on answer dialog - old?"
        addUserQuestion(old)
        val updated = "Test edit question is updated on answer dialog - updated?"
        editUserQuestion(old, updated)

        // when
        scrollToQuestion(updated)
        onView(withText(updated)).perform(click())

        // then
        onView(withId(R.id.questionTextOnAnswer))
            .check(matches(withText(updated)))

        // clean up
        pressBack()
        deleteUserQuestion(updated)
    }

    @Test
    fun editQuestion_isUpdatedInQuestionLog() {
        // given
        val old = "Test edit question is updated in log - old?"
        addUserQuestion(old)
        val updated = "Test edit question is updated in log - updated?"
        editUserQuestion(old, updated)
        answerQuestion(updated, "Test answer")

        // when
        openLogByQuestion(updated)

        // then it's updated in question log
        onView(withId(R.id.logByQuestionText))
            .check(matches(withText(updated)))

        // when
        pressBack()
        openFullLog()

        // then it's updated in full log
        onView(first(withId(R.id.logFullQuestionText)))
            .check(matches(withText(updated)))

        // clean up
        pressBack()
        deleteUserQuestion(updated, true)
    }

    @Test
    fun ediQuestionCancelled_isNotUpdated() {
        // given
        val old = "Test edit question cancelled is not updated?"
        addUserQuestion(old)

        // when
        openEditQuestionDialog(old)
        onView(withId(R.id.questionTextEdit))
            .perform(
                replaceText("Test edit question cancelled is not updated - new?"),
                closeSoftKeyboard()
            )
        onView(withText(R.string.btn_cancel)).perform(click())

        // then
        checkQuestion(old)

        // clean up
        deleteUserQuestion(old)
    }

    @Test
    fun tapOutsideEditQuestionDialog_popupIsNotClosed() {
        // given
        val question = "Test tap outside edit question dialog - popup is not closed?"
        addUserQuestion(question)
        openEditQuestionDialog(question)

        // when
        onView(withId(R.id.questionTextEdit))
            .perform(closeSoftKeyboard())
            .perform(clickXY(0, -600))

        // then
        onView(withId(R.id.questionTextEdit))
            .check(matches(isDisplayed()))

        // clean up
        onView(withText(R.string.btn_cancel)).perform(click())
        deleteUserQuestion(question)
    }

    @Test
    fun pressBackOnAddQuestionDialog_popupIsNotClosed() {
        // given
        val question = "Test press back on add question dialog - popup is not closed?"
        addUserQuestion(question)
        openEditQuestionDialog(question)

        // when
        pressBack()

        // then
        onView(withId(R.id.questionTextEdit))
            .check(matches(isDisplayed()))

        // clean up
        onView(withText(R.string.btn_cancel)).perform(click())
        deleteUserQuestion(question)
    }

    @Test
    fun popupMenuForFeelings_doesNotHaveEditMenu() {
        // when
        onView(
            Matchers.allOf(
                withId(R.id.popupMenu),
                hasSibling(withText(R.string.q_text_feelings))
            )
        )
            .perform(click())

        // then
        onView(withText(R.string.btn_edit)).check(doesNotExist())
    }

    @Test
    fun popupMenuForBuiltInQuestion_doesNotHaveEditMenu() {
        // when
        onView(
            Matchers.allOf(
                withId(R.id.popupMenu),
                hasSibling(withText(R.string.q_text_do_body))
            )
        )
            .perform(click())

        // then
        onView(withText(R.string.btn_edit)).check(doesNotExist())
    }

}