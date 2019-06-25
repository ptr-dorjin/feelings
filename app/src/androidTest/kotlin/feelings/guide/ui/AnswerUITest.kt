package feelings.guide.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AnswerUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun answerFeelings_addsLog() {
        // given
        val feeling = context.resources.getStringArray(R.array.anger_array)[1]
        addFeelingsAnswer(R.string.anger, feeling)

        // when
        openLogByQuestion(R.string.q_text_feelings)

        // then the answer is in question log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(feeling)))

        // when
        Espresso.pressBack()
        openFullLog()

        // then the answer is in full log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(feeling)))
    }

    @Test
    fun answerBuiltInQuestion_addsToLog() {
        // given
        val answer = "The app"
        addBuiltInAnswer(answer, R.string.q_text_do_others)

        // when
        openLogByQuestion(R.string.q_text_do_others)

        // then the answer is question log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(answer)))

        // when
        Espresso.pressBack()
        openFullLog()

        // then the answer is in full log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(answer)))
    }

    @Test
    fun answerUserQuestion_addsToLog() {
        // given
        val question = "Windows or Linux?"
        val answer = "Android"
        addUserQuestion(question)
        addUserAnswer(question, answer)

        // when
        openLogByQuestion(question)

        // then the answer is in question log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(answer)))

        // when
        Espresso.pressBack()
        openFullLog()

        // then the answer is in full log
        onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(answer)))

        // cleanup local
        Espresso.pressBack()
        deleteUserQuestion(question)
        onView(withText(R.string.btn_delete)).perform(ViewActions.click()) // delete answers too
    }
}