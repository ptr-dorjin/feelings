package feelings.guide.ui.ts06

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class DeleteUserQuestionUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun deleteQuestion_deletesFromList() {
        // given
        val question = "Test delete question - deletes from list?"
        addUserQuestion(question)

        // when
        deleteUserQuestion(question)

        // then
        checkNoQuestion(question)
    }

    @Test
    fun deleteQuestionWithAnswers_deletesAnswer() {
        // given
        val question = "Test delete question with answers - deletes answer?"
        addUserQuestion(question)
        val answer = "Test delete question with answers - delete answer."
        answerQuestion(question, answer)

        // when
        deleteUserQuestion(question, true, clearAnswers = true)

        // then
        openFullLog()
        checkNoAnswerInLogFull(answer)
    }

    @Test
    fun deleteQuestionWithoutAnswers_doesNotDeleteAnswer() {
        // given
        val question = "Test delete question without answers - does not delete answer?"
        addUserQuestion(question)
        val answer = "Test delete question without answers - does not delete answer."
        answerQuestion(question, answer)

        // when
        deleteUserQuestion(question, true, clearAnswers = false)

        // then
        openFullLog()
        checkLastAnswerInLogFull(answer)
    }

    @Test
    fun deleteAnswersForDeletedQuestions_answerIsDeleted() {
        // given
        val question = "Test delete answers for deleted questions - answer is deleted?"
        addUserQuestion(question)
        val answer = "Test delete answers for deleted questions - answer is deleted."
        answerQuestion(question, answer)
        deleteUserQuestion(question, true, clearAnswers = false)

        // when
        openFullLog()
        clearLogHiddenDeleted()

        // then
        checkNoAnswerInLogFull(answer)
    }

    @Test
    fun deleteCancelled_questionIsStillOnTheList() {
        // given
        val question = "Test delete cancelled - question is still on the list?"
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