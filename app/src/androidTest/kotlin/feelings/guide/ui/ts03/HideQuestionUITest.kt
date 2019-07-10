package feelings.guide.ui.ts03

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.util.*
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HideQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun hideBuiltInQuestion_questionDisappears() {
        // when
        hideQuestion(R.string.q_text_do_close)

        // then
        checkNoQuestion(R.string.q_text_do_close)
    }

    @Test
    fun hideBuiltInQuestion_answersAreNotDeleted() {
        // given
        val answer = "Push-ups"
        answerBuiltInQuestion(R.string.q_text_do_body, answer)

        // when
        hideQuestion(R.string.q_text_do_body)

        // then the answer is still in full log
        openFullLog()
        checkLastAnswer(answer)

        // clean up
        pressBack()
        restoreBuiltInQuestions()
    }

    @Test
    fun clearAnswersForHiddenQuestions_answerIsDeleted() {
        // given
        val answer = "Pull-ups"
        answerBuiltInQuestion(R.string.q_text_do_body, answer)
        hideQuestion(R.string.q_text_do_body)

        // when
        openFullLog()
        clearLogHiddenDeleted()

        // then the answer is gone
        checkNoAnswer(answer)

        // clean up
        pressBack()
        restoreBuiltInQuestions()
    }

    @Test
    fun restoreBuiltInQuestion_questionAppearsAgain() {
        // given
        hideQuestion(R.string.q_text_do_others)

        // when
        restoreBuiltInQuestions()

        // then
        pressBack()
        checkQuestion(R.string.q_text_do_others)
    }

    @Test
    fun hideCancelled_questionIsStillOnTheList() {
        // when
        scrollToQuestion(R.string.q_text_do_others)
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_do_others))))
            .perform(click())
        onView(withText(R.string.btn_hide)).perform(click())
        onView(withText(R.string.btn_cancel)).perform(click())

        // then
        checkQuestion(R.string.q_text_do_others)
    }

    @Test
    fun popupMenuForFeelings_doesNotHaveHideMenu() {
        // when
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_feelings))))
            .perform(click())

        // then
        onView(withText(R.string.btn_hide)).check(doesNotExist())
    }

    @Test
    fun popupMenuForUserQuestion_doesNotHaveHideMenu() {
        // given
        val question = "Bluetooth or wireless mouse?"
        addUserQuestion(question)

        // when
        scrollToQuestion(question)
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question))))
            .perform(click())

        // then
        onView(withText(R.string.btn_hide)).check(doesNotExist())

        // clean up
        pressBack()
        deleteUserQuestion(question)
    }
}