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
        val old = "To eat?"
        addUserQuestion(old)

        // when
        val updated = "To eat or to drink?"
        editUserQuestion(old, updated)

        // then
        checkQuestion(updated)

        // clean up
        deleteUserQuestion(updated)
    }

    @Test
    fun editQuestion_isUpdatedOnAnswerDialog() {
        // given
        val old = "Ale or lager?"
        addUserQuestion(old)
        val updated = "Anti Hero IPA or Goose Island IPA"
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
        val old = "HP or Lenovo?"
        addUserQuestion(old)
        val updated = "System76 or Dell?"
        editUserQuestion(old, updated)
        answerQuestion(updated, "System76")

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
        val old = "Mouse or TouchPad?"
        addUserQuestion(old)

        // when
        openEditQuestionDialog(old)
        onView(withId(R.id.questionTextEdit))
            .perform(replaceText("Mouse or trackball?"), closeSoftKeyboard())
        onView(withText(R.string.btn_cancel)).perform(click())

        // then
        checkQuestion(old)

        // clean up
        deleteUserQuestion(old)
    }

    @Test
    fun tapOutsideEditQuestionDialog_popupIsNotClosed() {
        // given
        val question = "Popup should not be closed when tap outside"
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
        val question = "Popup should not be closed when Back is pressed"
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
        onView(Matchers.allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_feelings))))
            .perform(click())

        // then
        onView(withText(R.string.btn_edit)).check(doesNotExist())
    }

    @Test
    fun popupMenuForBuiltInQuestion_doesNotHaveEditMenu() {
        // when
        onView(Matchers.allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_do_body))))
            .perform(click())

        // then
        onView(withText(R.string.btn_edit)).check(doesNotExist())
    }

}