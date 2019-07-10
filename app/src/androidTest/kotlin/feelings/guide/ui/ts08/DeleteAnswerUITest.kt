package feelings.guide.ui.ts08

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.util.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteAnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @get:Rule
    var answerLogRule = ActivityTestRule(AnswerLogActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun deleteAnswer_fullLog_deletesFromList() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()

        // when
        deleteAnswer(answer)

        // then
        checkSnackbar(R.string.msg_answer_deleted_success)
        checkNoAnswer(answer)
    }

    @FlakyTest(detail = "Snackbar is not rendered sometimes")
    @Test
    fun undoAnswerDeletion_fullLog_returnsAnswerToList() {
        // given
        val answer = answerFeelingsRandom()
        openFullLog()
        deleteAnswer(answer)

        // when
        onView(withText(R.string.snackbar_undo)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteAnswer_questionLog_deletesFromList() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        // when
        deleteAnswer(answer)

        // then
        checkSnackbar(R.string.msg_answer_deleted_success)
        checkNoAnswer(answer)
    }

    @Test
    fun undoAnswerDeletion_questionLog_returnsAnswerToList() {
        // given
        val answer = answerFeelingsRandom()
        openLogByQuestion(R.string.q_text_feelings)
        deleteAnswer(answer)

        // when
        onView(withText(R.string.snackbar_undo)).perform(click())

        // then
        onView(withText(answer)).check(matches(isDisplayed()))
    }
}