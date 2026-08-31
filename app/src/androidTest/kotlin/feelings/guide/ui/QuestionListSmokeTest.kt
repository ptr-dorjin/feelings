package feelings.guide.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Confirms the app boots to the Questions screen with its seeded built-in questions. Asserts via
 * testTag rather than text: the (closed) drawer's own "Questions" nav item stays composed off-
 * screen alongside the main content, so a plain text match is ambiguous.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class QuestionListSmokeTest : BaseComposeUiTest() {

    @Test
    fun questionsScreen_showsSeededBuiltInQuestions() {
        composeRule.onNodeWithTag("questionsList").assertExists()
        composeRule.onNodeWithTag("questionCard_What do I feel right now?").assertExists()
        composeRule.onNodeWithTag("questionCard_Who can I thank?").assertExists()
    }
}
