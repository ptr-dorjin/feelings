package feelings.guide.ui.ts05

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.answerQuestion
import feelings.guide.ui.checkQuestion
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.editUserQuestion
import feelings.guide.ui.openAnswerScreen
import feelings.guide.ui.openEditQuestionDialog
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.randomAlphanumericString
import feelings.guide.ui.scrollToQuestion
import feelings.guide.ui.textOfFirstWithTag
import feelings.guide.ui.textOfTag
import feelings.guide.ui.waitUntilBackOnQuestionsScreen
import feelings.guide.ui.waitUntilGone
import feelings.guide.ui.waitUntilTextExists
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EditUserQuestionUITest : BaseComposeUiTest() {

    @Test
    fun editQuestion_isUpdatedOnQuestionList() {
        val old = "Test edit question - old ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(old)

        val updated = "Test edit question - updated ${randomAlphanumericString()}?"
        composeRule.editUserQuestion(old, updated)

        composeRule.checkQuestion(updated)
        composeRule.deleteUserQuestion(updated)
    }

    @Test
    fun editQuestion_isUpdatedOnAnswerScreen() {
        val old = "Test edit question updated on answer screen - old ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(old)
        val updated = "Test edit question updated on answer screen - updated ${randomAlphanumericString()}?"
        composeRule.editUserQuestion(old, updated)

        composeRule.openAnswerScreen(updated)

        assertThat(composeRule.textOfTag("answerQuestionHeadline")).isEqualTo(updated)

        // The non-feelings answer field auto-focuses on open, showing the IME; the first back
        // press only dismisses that keyboard rather than navigating (standard Android behavior).
        closeSoftKeyboard()
        pressBack()
        composeRule.waitUntilBackOnQuestionsScreen()
        composeRule.deleteUserQuestion(updated)
    }

    @Test
    fun editQuestion_isUpdatedInLogs() {
        val old = "Test edit question updated in log - old ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(old)
        val updated = "Test edit question updated in log - updated ${randomAlphanumericString()}?"
        composeRule.editUserQuestion(old, updated)
        composeRule.answerQuestion(updated, "Test answer")

        composeRule.openLogByQuestion(updated)
        composeRule.waitUntilTextExists(updated)
        assertThat(composeRule.textOfTag("logHeaderQuestionText")).isEqualTo(updated)

        pressBack()
        composeRule.openFullLog()
        composeRule.waitUntilTextExists(updated)
        assertThat(composeRule.textOfFirstWithTag("logRowQuestionText")).isEqualTo(updated)

        pressBack()
        composeRule.deleteUserQuestion(updated, hasAnswers = true)
    }

    @Test
    fun editQuestionCancelled_isNotUpdated() {
        val old = "Test edit question cancelled is not updated ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(old)

        composeRule.openEditQuestionDialog(old)
        composeRule.onNodeWithTag("questionSheetTextField")
            .performTextReplacement("Test edit question cancelled is not updated - new?")
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.btn_cancel)).performClick()

        composeRule.checkQuestion(old)
        composeRule.deleteUserQuestion(old)
    }

    /**
     * The redesign's bottom sheet dismisses on back press by default (standard Material3
     * ModalBottomSheet behavior) rather than staying open like the legacy AlertDialog — see the
     * equivalent note in AddUserQuestionUITest.
     */
    @Test
    fun pressBackOnEditQuestionSheet_sheetIsDismissed() {
        val question = "Test press back on edit question sheet ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        composeRule.openEditQuestionDialog(question)

        pressBack()

        composeRule.waitUntilGone("questionSheetTextField")
        composeRule.deleteUserQuestion(question)
    }

    @Test
    fun feelingsQuestion_hasNoEditIcon() {
        val question = composeRule.activity.getString(R.string.q_text_feelings)
        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionEdit_$question").assertDoesNotExist()
    }

    @Test
    fun builtInQuestion_hasNoEditIcon() {
        val question = composeRule.activity.getString(R.string.q_text_do_body)
        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionEdit_$question").assertDoesNotExist()
    }
}
