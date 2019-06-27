package feelings.guide.ui.ts03

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.addBuiltInAnswer
import feelings.guide.ui.checkLastAnswer
import feelings.guide.ui.checkNoAnswer
import feelings.guide.ui.checkNoQuestion
import feelings.guide.ui.checkQuestion
import feelings.guide.ui.clearLogHiddenDeleted
import feelings.guide.ui.hideQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.restoreBuiltInQuestions
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
        addBuiltInAnswer(R.string.q_text_do_body, answer)

        // when
        hideQuestion(R.string.q_text_do_body)

        // then the answer is still in full log
        openFullLog()
        checkLastAnswer(answer)

        // clean up
        Espresso.pressBack()
        restoreBuiltInQuestions()
    }

    @Test
    fun clearAnswersForHiddenQuestions_answerIsDeleted() {
        // given
        val answer = "Pull-ups"
        addBuiltInAnswer(R.string.q_text_do_body, answer)
        hideQuestion(R.string.q_text_do_body)

        // when
        openFullLog()
        clearLogHiddenDeleted()

        // then the answer is gone
        checkNoAnswer(answer)

        // clean up
        Espresso.pressBack()
        restoreBuiltInQuestions()
    }

    @Test
    fun restoreBuiltInQuestion_questionAppearsAgain() {
        // given
        hideQuestion(R.string.q_text_do_others)

        // when
        restoreBuiltInQuestions()

        // then
        Espresso.pressBack()
        checkQuestion(R.string.q_text_do_others)
    }

}