package feelings.guide.ui.ts02

import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.answerBuiltInQuestion
import feelings.guide.ui.answerFeelings
import feelings.guide.ui.answerQuestion
import feelings.guide.ui.checkNoAnswerInLog
import feelings.guide.ui.clearLog
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.randomAlphanumericString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The legacy suite also covered a "clear log from the question list" quick action via a per-card
 * popup menu; the redesigned card only exposes Log/Edit/Delete(or Hide) icons (mockup 01), so
 * clearing a question's log now only happens from within that question's own log screen — the
 * "FromQuestionLog" scenarios below cover the same ground.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ClearLogUITest : BaseComposeUiTest() {

    @Test
    fun clearLogFromQuestionLog_feelings() {
        val feeling = composeRule.activity.resources.getStringArray(R.array.anger_array)[1]
        composeRule.answerFeelings(R.string.anger, feeling)

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.clearLog()

        composeRule.checkNoAnswerInLog(feeling)
    }

    @Test
    fun clearLogFull_feelings() {
        val feeling = composeRule.activity.resources.getStringArray(R.array.anger_array)[1]
        composeRule.answerFeelings(R.string.anger, feeling)

        composeRule.openFullLog()
        composeRule.clearLog()

        composeRule.checkNoAnswerInLog(feeling)
    }

    @Test
    fun clearLogFromQuestionLog_builtIn() {
        val answer = "Test clear log from question log ${randomAlphanumericString()}."
        composeRule.answerBuiltInQuestion(R.string.q_text_do_body, answer)

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_do_body))
        composeRule.clearLog()

        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun clearLogFull_builtIn() {
        val answer = "Test clear log full ${randomAlphanumericString()}."
        composeRule.answerBuiltInQuestion(R.string.q_text_gratitude, answer)

        composeRule.openFullLog()
        composeRule.clearLog()

        composeRule.checkNoAnswerInLog(answer)
    }

    @Test
    fun clearLogFromQuestionLog_userQuestion() {
        val question = "Test clear log from question log user question ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, "Test answer.")

        composeRule.openLogByQuestion(question)
        composeRule.clearLog()
        composeRule.checkNoAnswerInLog("Test answer.")

        pressBack()
        composeRule.deleteUserQuestion(question)
    }

    @Test
    fun clearLogFull_userQuestion() {
        val question = "Test clear log full user question ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        composeRule.answerQuestion(question, "Test answer.")

        composeRule.openFullLog()
        composeRule.clearLog()
        composeRule.checkNoAnswerInLog("Test answer.")

        pressBack()
        composeRule.deleteUserQuestion(question)
    }
}
