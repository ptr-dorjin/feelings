package feelings.guide.ui.ts04

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.BaseComposeUiTest
import feelings.guide.ui.addUserQuestion
import feelings.guide.ui.checkNoQuestion
import feelings.guide.ui.checkQuestion
import feelings.guide.ui.checkSaveDisabledInQuestionSheet
import feelings.guide.ui.deleteUserQuestion
import feelings.guide.ui.randomAlphanumericString
import feelings.guide.ui.waitUntilExists
import feelings.guide.ui.waitUntilGone
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddUserQuestionUITest : BaseComposeUiTest() {

    @Test
    fun addUserQuestion_appearsInList() {
        val question = "Test add user question appears in the list ${randomAlphanumericString()}?"
        composeRule.addUserQuestion(question)
        composeRule.checkQuestion(question)
        composeRule.deleteUserQuestion(question)
    }

    @Test
    fun emptyText_saveBtnIsDisabled() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_add_question)).performClick()
        composeRule.waitUntilExists("questionSheetTextField")
        composeRule.checkSaveDisabledInQuestionSheet()
    }

    @Test
    fun blankText_saveBtnIsDisabled() {
        val blank = "   "
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_add_question)).performClick()
        composeRule.waitUntilExists("questionSheetTextField")
        composeRule.onNodeWithTag("questionSheetTextField").performTextInput(blank)

        composeRule.checkSaveDisabledInQuestionSheet()

        composeRule.onNodeWithText(composeRule.activity.getString(R.string.btn_cancel)).performClick()
        composeRule.checkNoQuestion(blank)
    }

    /**
     * The redesign uses a Material3 ModalBottomSheet (mockup 09) instead of an AlertDialog, and
     * standard bottom-sheet behavior is to dismiss on scrim tap or back press — unlike the legacy
     * dialog, which intentionally stayed open. That's a deliberate consequence of the new
     * component, not a regression, so it's covered directly rather than porting the old
     * "popup is not closed" expectation.
     */
    @Test
    fun pressBackOnAddQuestionSheet_sheetIsDismissed() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.cd_add_question)).performClick()
        composeRule.waitUntilExists("questionSheetTextField")

        pressBack()

        composeRule.waitUntilGone("questionSheetTextField")
    }
}
