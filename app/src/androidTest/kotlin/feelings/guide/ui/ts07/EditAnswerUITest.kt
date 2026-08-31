package feelings.guide.ui.ts07

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.answerFeelingsRandom
import feelings.guide.ui.answerQuestion
import feelings.guide.ui.checkNoSnackbar
import feelings.guide.ui.checkSnackbar
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.editAnswer
import feelings.guide.ui.openEditAnswer
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.randomAlphanumericString
import feelings.guide.ui.waitUntilTextExists
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EditAnswerUITest : BaseComposeUiTest() {

    @Test
    fun openEditAnswerForFeelings_fullLog() {
        val answer = composeRule.answerFeelingsRandom()

        composeRule.openFullLog()
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithTag("answerTextField").assertTextEquals(answer)
        composeRule.onNodeWithTag("feelingsGroup_${composeRule.activity.getString(R.string.anger)}").assertExists()
    }

    @Test
    fun openEditAnswerForFeelings_questionLog() {
        val answer = composeRule.answerFeelingsRandom()

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithTag("answerTextField").assertTextEquals(answer)
        composeRule.onNodeWithTag("feelingsGroup_${composeRule.activity.getString(R.string.anger)}").assertExists()
    }

    @Test
    fun openEditAnswerForUserQuestion_fullLog() {
        val question = "Test open edit answer user question full log ${randomAlphanumericString()}?"
        val answer = "Test open edit answer user question full log ${randomAlphanumericString()}."
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, answer)

        composeRule.openFullLog()
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithTag("answerTextField").assertTextEquals(answer)

        // The non-feelings answer field auto-focuses on open, showing the IME; the first back
        // press only dismisses that keyboard rather than navigating (standard Android behavior).
        closeSoftKeyboard()
        pressBack()
        pressBack()
        composeRule.deleteUserQuestion(question, hasAnswers = true)
    }

    @Test
    fun openEditAnswerForUserQuestion_questionLog() {
        val question = "Test open edit answer user question question log ${randomAlphanumericString()}?"
        val answer = "Test open edit answer user question question log ${randomAlphanumericString()}."
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, answer)

        composeRule.openLogByQuestion(question)
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithTag("answerTextField").assertTextEquals(answer)

        closeSoftKeyboard()
        pressBack()
        pressBack()
        composeRule.deleteUserQuestion(question, hasAnswers = true)
    }

    @Test
    fun saveEditAnswerForFeelings_fullLog() {
        val answer = composeRule.answerFeelingsRandom()
        val new = randomAlphanumericString()

        composeRule.openFullLog()
        composeRule.editAnswer(answer, new)

        composeRule.checkSnackbar(R.string.msg_answer_updated_success)
        composeRule.waitUntilTextExists(new)
    }

    @Test
    fun saveEditAnswerForFeelings_questionLog() {
        val answer = composeRule.answerFeelingsRandom()
        val new = randomAlphanumericString()

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.editAnswer(answer, new)

        composeRule.checkSnackbar(R.string.msg_answer_updated_success)
        composeRule.waitUntilTextExists(new)
    }

    @Test
    fun saveEditAnswerForUserQuestion_fullLog() {
        val question = "Test save edit answer user question full log ${randomAlphanumericString()}?"
        val answer = "Test save edit answer user question full log ${randomAlphanumericString()}."
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, answer)
        val new = "Test save edit answer user question full log - updated ${randomAlphanumericString()}."

        composeRule.openFullLog()
        composeRule.editAnswer(answer, new)

        composeRule.checkSnackbar(R.string.msg_answer_updated_success)
        composeRule.waitUntilTextExists(new)

        pressBack()
        composeRule.deleteUserQuestion(question, hasAnswers = true)
    }

    @Test
    fun saveEditAnswerForUserQuestion_questionLog() {
        val question = "Test save edit answer user question question log ${randomAlphanumericString()}?"
        val answer = "Test save edit answer user question question log ${randomAlphanumericString()}."
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, answer)
        val new = "Test save edit answer user question question log - updated ${randomAlphanumericString()}."

        composeRule.openLogByQuestion(question)
        composeRule.editAnswer(answer, new)

        composeRule.checkSnackbar(R.string.msg_answer_updated_success)
        composeRule.waitUntilTextExists(new)

        pressBack()
        composeRule.deleteUserQuestion(question, hasAnswers = true)
    }

    @Test
    fun pressBackOnEditAnswerForFeelings_fullLog() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openFullLog()
        composeRule.openEditAnswer(answer)

        pressBack()

        composeRule.waitUntilTextExists(answer)
        composeRule.checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressBackOnEditAnswerForFeelings_questionLog() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.openEditAnswer(answer)

        pressBack()

        composeRule.waitUntilTextExists(answer)
        composeRule.checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressUpOnEditAnswerForFeelings_fullLog() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openFullLog()
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_back)).performClick()

        composeRule.waitUntilTextExists(answer)
        composeRule.checkNoSnackbar(R.string.msg_answer_updated_success)
    }

    @Test
    fun pressUpOnEditAnswerForFeelings_questionLog() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.openEditAnswer(answer)

        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_back)).performClick()

        composeRule.waitUntilTextExists(answer)
        composeRule.checkNoSnackbar(R.string.msg_answer_updated_success)
    }
}
