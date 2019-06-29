package feelings.guide.ui.ts03

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.*
import feelings.guide.ui.question.QuestionsActivity
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

}