package feelings.guide.ui.ts06

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.*
import feelings.guide.ui.question.QuestionsActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteUserQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun deleteQuestion_deletesFromList() {
        // given
        val question = "Manual or automatic transmission?"
        addUserQuestion(question)

        // when
        deleteUserQuestion(question)

        // then
        checkNoQuestion(question)
    }

    @Test
    fun deleteQuestionWithAnswers_deletesAnswer() {
        // given
        val question = "Parallel or perpendicular parking?"
        addUserQuestion(question)
        val answer = "Perpendicular"
        answerQuestion(question, answer)

        // when
        deleteUserQuestion(question, true, clearAnswers = true)

        // then
        openFullLog()
        checkNoAnswer(answer)
    }

    @Test
    fun deleteQuestionWithoutAnswers_doesNotDeleteAnswer() {
        // given
        val question = "Windshield or dashboard phone mount?"
        addUserQuestion(question)
        val answer = "Vent mount"
        answerQuestion(question, answer)

        // when
        deleteUserQuestion(question, true, clearAnswers = false)

        // then
        openFullLog()
        checkLastAnswer(answer)
    }

    @Test
    fun deleteAnswersForDeletedQuestions_answerIsDeleted() {
        // given
        val question = "ZipCar or Maven?"
        addUserQuestion(question)
        val answer = "Car2go"
        answerQuestion(question, answer)
        deleteUserQuestion(question, true, clearAnswers = false)

        // when
        openFullLog()
        clearLogHiddenDeleted()

        // then
        checkNoAnswer(answer)
    }

    @Test
    fun deleteCancelled_questionIsStillOnTheList() {
        // given
        val question = "Lyft or Uber?"
        addUserQuestion(question)

        // when
        scrollToQuestion(question)
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question))))
            .perform(click())
        onView(withText(R.string.btn_delete)).perform(click())
        onView(withText(R.string.btn_cancel)).perform(click())

        // then
        checkQuestion(question)

        // clean up
        deleteUserQuestion(question)
    }

    @Test
    fun popupMenuForFeelings_doesNotHaveDeleteMenu() {
        // when
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_feelings))))
            .perform(click())

        // then
        onView(withText(R.string.btn_delete)).check(doesNotExist())
    }

    @Test
    fun popupMenuForBuiltInQuestion_doesNotHaveDeleteMenu() {
        // when
        onView(allOf(withId(R.id.popupMenu), hasSibling(withText(R.string.q_text_do_body))))
            .perform(click())

        // then
        onView(withText(R.string.btn_delete)).check(doesNotExist())
    }
}