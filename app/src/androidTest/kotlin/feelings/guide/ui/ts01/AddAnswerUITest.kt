package feelings.guide.ui.ts01

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.addAnswer
import feelings.guide.ui.addBuiltInAnswer
import feelings.guide.ui.addFeelingsAnswer
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.checkLastAnswer
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.question.QuestionsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddAnswerUITest {
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
        checkLastAnswer(feeling)

        // when
        Espresso.pressBack()
        openFullLog()

        // then the answer is in full log
        checkLastAnswer(feeling)
    }

    @Test
    fun answerBuiltInQuestion_addsToLog() {
        // given
        val answer = "The app"
        addBuiltInAnswer(R.string.q_text_do_others, answer)

        // when
        openLogByQuestion(R.string.q_text_do_others)

        // then the answer is question log
        checkLastAnswer(answer)

        // when
        Espresso.pressBack()
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
        addAnswer(question, answer)

        // when
        openLogByQuestion(question)

        // then the answer is in question log
        checkLastAnswer(answer)

        // when
        Espresso.pressBack()
        openFullLog()

        // then the answer is in full log
        checkLastAnswer(answer)

        // clean up user question
        Espresso.pressBack()
        deleteUserQuestion(question, true)
    }
}