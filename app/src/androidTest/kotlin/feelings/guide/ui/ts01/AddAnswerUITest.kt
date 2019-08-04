package feelings.guide.ui.ts01

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@LargeTest
class AddAnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun answerFeelings_addsLog() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        answerFeelings(R.string.anger, feeling)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(R.string.q_text_feelings)

        // then the answer is in question log
        checkLastAnswer(feeling)

        // when
        pressBack()
        openFullLog()

        // then the answer is in full log
        checkLastAnswer(feeling)
    }

    @Test
    fun answerBuiltInQuestion_addsToLog() {
        // given
        val answer = "The app"
        answerBuiltInQuestion(R.string.q_text_do_others, answer)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(R.string.q_text_do_others)

        // then the answer is question log
        checkLastAnswer(answer)

        // when
        pressBack()
        openFullLog()

        // then the answer is in full log
        checkLastAnswer(answer)
    }

    @Test
    fun answerUserQuestion_addsToLog() {
        // given
        val question = "Windows or Linux?"
        val answer = "Android"
        addUserQuestion(question)
        answerQuestion(question, answer)

        // then
        checkSnackbar(R.string.msg_answer_added_success)

        // when
        openLogByQuestion(question)

        // then the answer is in question log
        checkLastAnswer(answer)

        // when
        pressBack()
        openFullLog()

        // then the answer is in full log
        checkLastAnswer(answer)

        // clean up user question
        pressBack()
        deleteUserQuestion(question, true)
    }

    @Test
    fun pressUpOnAddAnswer_noSnackbarIsShown() {
        // given
        onView(withText(R.string.q_text_feelings)).perform(click())

        // when
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        // then
        checkNoSnackbar(R.string.msg_answer_added_success)
    }

    @Test
    fun pressBackOnAddAnswer_noSnackbarIsShown() {
        // given
        onView(withText(R.string.q_text_feelings)).perform(click())

        // when
        pressBack()

        // then
        checkNoSnackbar(R.string.msg_answer_added_success)
    }
}