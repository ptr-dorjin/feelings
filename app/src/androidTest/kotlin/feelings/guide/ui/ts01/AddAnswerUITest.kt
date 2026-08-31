package feelings.guide.ui.ts01

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.answerBuiltInQuestion
import feelings.guide.ui.answerFeelings
import feelings.guide.ui.answerQuestion
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.checkLastAnswerInLog
import feelings.guide.ui.checkNoSnackbar
import feelings.guide.ui.checkSnackbar
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.openAnswerScreen
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.randomAlphanumericString
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddAnswerUITest : BaseComposeUiTest() {

    @Test
    fun answerFeelings_addsLog() {
        val feeling = composeRule.activity.resources.getStringArray(R.array.anger_array)[1]
        composeRule.answerFeelings(R.string.anger, feeling)
        composeRule.checkSnackbar(R.string.msg_answer_added_success)

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.checkLastAnswerInLog(feeling)

        pressBack()
        composeRule.openFullLog()
        composeRule.checkLastAnswerInLog(feeling)
    }

    @Test
    fun answerBuiltInQuestion_addsToLog() {
        val answer = "Test answer built-in question ${randomAlphanumericString()}."
        composeRule.answerBuiltInQuestion(R.string.q_text_do_others, answer)
        composeRule.checkSnackbar(R.string.msg_answer_added_success)

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_do_others))
        composeRule.checkLastAnswerInLog(answer)

        pressBack()
        composeRule.openFullLog()
        composeRule.checkLastAnswerInLog(answer)
    }

    @Test
    fun answerUserQuestion_addsToLog() {
        val question = "Test answer user question adds to log ${randomAlphanumericString()}?"
        val answer = "Test answer user question ${randomAlphanumericString()}."
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, answer)
        composeRule.checkSnackbar(R.string.msg_answer_added_success)

        composeRule.openLogByQuestion(question)
        composeRule.checkLastAnswerInLog(answer)

        pressBack()
        composeRule.openFullLog()
        composeRule.checkLastAnswerInLog(answer)

        pressBack()
        composeRule.deleteUserQuestion(question, hasAnswers = true)
    }

    @Test
    fun pressUpOnAddAnswer_noSnackbarIsShown() {
        composeRule.openAnswerScreen(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_back)).performClick()
        composeRule.checkNoSnackbar(R.string.msg_answer_added_success)
    }

    @Test
    fun pressBackOnAddAnswer_noSnackbarIsShown() {
        composeRule.openAnswerScreen(composeRule.activity.getString(R.string.q_text_feelings))
        pressBack()
        composeRule.checkNoSnackbar(R.string.msg_answer_added_success)
    }
}
