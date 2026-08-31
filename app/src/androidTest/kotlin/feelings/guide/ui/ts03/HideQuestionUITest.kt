package feelings.guide.ui.ts03

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.answerBuiltInQuestion
import feelings.guide.ui.checkLastAnswerInLog
import feelings.guide.ui.checkNoAnswerInLog
import feelings.guide.ui.checkNoQuestion
import feelings.guide.ui.checkQuestion
import feelings.guide.ui.clearDeletedAnswers
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.hideQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.randomAlphanumericString
import feelings.guide.ui.restoreBuiltInQuestions
import feelings.guide.ui.scrollToQuestion
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HideQuestionUITest : BaseComposeUiTest() {

    @Test
    fun hideBuiltInQuestion_questionDisappears() {
        val question = composeRule.activity.getString(R.string.q_text_do_close)
        composeRule.hideQuestion(question)
        composeRule.checkNoQuestion(question)
        composeRule.restoreBuiltInQuestions()
    }

    @Test
    fun hideBuiltInQuestion_answersAreNotDeleted() {
        val answer = "Test hide built-in question ${randomAlphanumericString()}."
        composeRule.answerBuiltInQuestion(R.string.q_text_do_body, answer)

        composeRule.hideQuestion(composeRule.activity.getString(R.string.q_text_do_body))

        composeRule.openFullLog()
        composeRule.checkLastAnswerInLog(answer)

        pressBack()
        composeRule.restoreBuiltInQuestions()
    }

    @Test
    fun clearAnswersForHiddenQuestions_answerIsDeleted() {
        val answer = "Test clear answers for hidden questions ${randomAlphanumericString()}."
        composeRule.answerBuiltInQuestion(R.string.q_text_do_body, answer)
        composeRule.hideQuestion(composeRule.activity.getString(R.string.q_text_do_body))

        composeRule.openFullLog()
        composeRule.clearDeletedAnswers()
        composeRule.checkNoAnswerInLog(answer)

        pressBack()
        composeRule.restoreBuiltInQuestions()
    }

    @Test
    fun restoreBuiltInQuestion_questionAppearsAgain() {
        val question = composeRule.activity.getString(R.string.q_text_do_others)
        composeRule.hideQuestion(question)

        composeRule.restoreBuiltInQuestions()

        composeRule.checkQuestion(question)
    }

    @Test
    fun hideCancelled_questionIsStillOnTheList() {
        val question = composeRule.activity.getString(R.string.q_text_do_others)
        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionHide_$question").performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.btn_cancel)).performClick()

        composeRule.checkQuestion(question)
    }

    /**
     * Unlike the legacy app, the redesign (mockup 01) shows a hide icon on every built-in card,
     * including the feelings question — there's no special protection for it anymore, so the
     * legacy "feelings has no hide menu" scenario no longer applies and isn't ported.
     */
    @Test
    fun userQuestion_hasNoHideIcon() {
        val question = "Test user question has no hide icon ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)

        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionHide_$question").assertDoesNotExist()

        composeRule.deleteUserQuestion(question)
    }
}
