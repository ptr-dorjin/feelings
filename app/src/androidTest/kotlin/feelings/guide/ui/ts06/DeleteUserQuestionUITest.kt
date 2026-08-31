package feelings.guide.ui.ts06

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.answerQuestion
import feelings.guide.ui.checkLastAnswerInLog
import feelings.guide.ui.checkNoAnswerInLog
import feelings.guide.ui.checkNoQuestion
import feelings.guide.ui.checkQuestion
import feelings.guide.ui.clearDeletedAnswers
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.randomAlphanumericString
import feelings.guide.ui.scrollToQuestion
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DeleteUserQuestionUITest : BaseComposeUiTest() {

    @Test
    fun deleteQuestion_deletesFromList() {
        val question = "Test delete question deletes from list ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        composeRule.deleteUserQuestion(question)
        composeRule.checkNoQuestion(question)
    }

    @Test
    fun deleteQuestionWithAnswers_deletesAnswer() {
        val question = "Test delete question with answers ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        val answer = "Test delete question with answers - delete answer ${randomAlphanumericString()}."
        composeRule.answerQuestion(question, answer)

        composeRule.deleteUserQuestion(question, hasAnswers = true, clearAnswers = true)

        composeRule.openFullLog()
        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun deleteQuestionWithoutAnswers_doesNotDeleteAnswer() {
        val question = "Test delete question without answers ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        val answer = "Test delete question without answers - keeps answer ${randomAlphanumericString()}."
        composeRule.answerQuestion(question, answer)

        composeRule.deleteUserQuestion(question, hasAnswers = true, clearAnswers = false)

        composeRule.openFullLog()
        composeRule.checkLastAnswerInLog(answer)
    }

    @Test
    fun deleteAnswersForDeletedQuestions_answerIsDeleted() {
        val question = "Test delete answers for deleted questions ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        val answer = "Test delete answers for deleted questions - answer ${randomAlphanumericString()}."
        composeRule.answerQuestion(question, answer)
        composeRule.deleteUserQuestion(question, hasAnswers = true, clearAnswers = false)

        composeRule.openFullLog()
        composeRule.clearDeletedAnswers()

        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun deleteCancelled_questionIsStillOnTheList() {
        val question = "Test delete cancelled - question is still on the list ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)

        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionDelete_$question").performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.btn_cancel)).performClick()

        composeRule.checkQuestion(question)
        composeRule.deleteUserQuestion(question)
    }

    @Test
    fun feelingsQuestion_hasNoDeleteIcon() {
        val question = composeRule.activity.getString(R.string.q_text_feelings)
        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionDelete_$question").assertDoesNotExist()
    }

    @Test
    fun builtInQuestion_hasNoDeleteIcon() {
        val question = composeRule.activity.getString(R.string.q_text_do_body)
        composeRule.scrollToQuestion(question)
        composeRule.onNodeWithTag("questionDelete_$question").assertDoesNotExist()
    }
}
