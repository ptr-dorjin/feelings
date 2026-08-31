package feelings.guide.ui.ts08

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.answerFeelingsRandom
import feelings.guide.ui.checkLastAnswerInLog
import feelings.guide.ui.checkNoAnswerInLog
import feelings.guide.ui.checkSnackbar
import feelings.guide.ui.deleteAnswer
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.undoAnswerDeletion
import feelings.guide.ui.waitUntilTextExists
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DeleteAnswerUITest : BaseComposeUiTest() {

    @Test
    fun deleteAnswer_fullLog_deletesFromList() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openFullLog()

        composeRule.deleteAnswer(answer)

        composeRule.checkSnackbar(R.string.msg_answer_deleted_success)
        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun undoAnswerDeletion_fullLog_returnsAnswerToList() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openFullLog()
        composeRule.deleteAnswer(answer)

        composeRule.waitUntilTextExists(composeRule.activity.getString(R.string.snackbar_undo))
        composeRule.undoAnswerDeletion()

        composeRule.checkLastAnswerInLog(answer)
    }

    @Test
    fun deleteAnswer_questionLog_deletesFromList() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))

        composeRule.deleteAnswer(answer)

        composeRule.checkSnackbar(R.string.msg_answer_deleted_success)
        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun undoAnswerDeletion_questionLog_returnsAnswerToList() {
        val answer = composeRule.answerFeelingsRandom()
        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.deleteAnswer(answer)

        composeRule.waitUntilTextExists(composeRule.activity.getString(R.string.snackbar_undo))
        composeRule.undoAnswerDeletion()

        composeRule.checkLastAnswerInLog(answer)
    }
}
