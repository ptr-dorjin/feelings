package feelings.guide.ui

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.rules.ActivityScenarioRule
import feelings.guide.R
import kotlin.random.Random

typealias ComposeRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomAlphanumericString(stringLength: Int = 8): String =
    (1..stringLength).map { charPool[Random.nextInt(charPool.size)] }.joinToString("")

private fun ComposeRule.str(resId: Int): String = activity.getString(resId)

fun ComposeRule.waitUntilExists(tag: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) { onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
}

fun ComposeRule.waitUntilTextExists(text: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
}

fun ComposeRule.waitUntilGone(tag: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) { onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty() }
}

fun ComposeRule.waitUntilTextGone(text: String, timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) { onAllNodesWithText(text).fetchSemanticsNodes().isEmpty() }
}

// ---- Questions screen ----

fun ComposeRule.scrollToQuestion(question: String) {
    onNodeWithTag("questionsList").performScrollToNode(hasTestTag("questionCard_$question"))
}

/**
 * Like [scrollToQuestion], but tolerant of the item not existing in the underlying list *at all*
 * yet — e.g. right after creating it, before the Room write has propagated back through the Flow
 * into the LazyColumn's data. performScrollToNode alone can only bring an already-present-but-
 * off-screen item into view; it can't wait for the item to exist in the first place.
 */
fun ComposeRule.waitAndScrollToQuestion(question: String, timeoutMillis: Long = 8000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    var lastError: Throwable? = null
    while (System.currentTimeMillis() < deadline) {
        try {
            scrollToQuestion(question)
            return
        } catch (e: Throwable) {
            lastError = e
            Thread.sleep(200)
        }
    }
    throw lastError ?: AssertionError("Timed out waiting for question card: $question")
}

/**
 * Scrolls the specific icon into view (not just its containing card): a tall card can have its
 * top scrolled into the viewport while an icon near its bottom is still clipped off-screen, in
 * which case `performClick()` silently fails to land — this scrolls to the exact target instead.
 */
private fun ComposeRule.scrollToQuestionIcon(tag: String, timeoutMillis: Long = 8000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    var lastError: Throwable? = null
    while (System.currentTimeMillis() < deadline) {
        try {
            onNodeWithTag("questionsList").performScrollToNode(hasTestTag(tag))
            return
        } catch (e: Throwable) {
            lastError = e
            Thread.sleep(200)
        }
    }
    throw lastError ?: AssertionError("Timed out waiting for question icon: $tag")
}

fun ComposeRule.checkQuestion(question: String) {
    waitAndScrollToQuestion(question)
    onNodeWithTag("questionCard_$question").assertExists()
}

fun ComposeRule.checkNoQuestion(question: String) {
    onNodeWithTag("questionCard_$question").assertDoesNotExist()
}

fun ComposeRule.addUserQuestion(question: String) {
    onNodeWithContentDescription(str(R.string.cd_add_question)).performClick()
    // ModalBottomSheet content also renders in a separate window; wait for it to attach.
    waitUntilExists("questionSheetTextField")
    onNodeWithTag("questionSheetTextField").performTextInput(question)
    onNodeWithTag("questionSheetSaveButton").performClick()
    waitUntilGone("questionSheetTextField")
    // The sheet closing only confirms the dismiss animation ran, not that the Room write has
    // propagated back through the Flow into the LazyColumn yet.
    waitAndScrollToQuestion(question)
}

fun ComposeRule.openEditQuestionDialog(question: String) {
    scrollToQuestionIcon("questionEdit_$question")
    onNodeWithTag("questionEdit_$question").performClick()
    waitUntilExists("questionSheetTextField")
}

fun ComposeRule.editUserQuestion(old: String, new: String) {
    openEditQuestionDialog(old)
    onNodeWithTag("questionSheetTextField").performTextReplacement(new)
    onNodeWithTag("questionSheetSaveButton").performClick()
    waitUntilGone("questionSheetTextField")
    waitAndScrollToQuestion(new)
}

/**
 * Mirrors the legacy two-outcome flow, adapted to the merged delete dialog: [hasAnswers] controls
 * whether the checkbox is expected to be present, [clearAnswers] whether it gets checked.
 */
fun ComposeRule.deleteUserQuestion(question: String, hasAnswers: Boolean = false, clearAnswers: Boolean = true) {
    scrollToQuestionIcon("questionDelete_$question")
    onNodeWithTag("questionDelete_$question").performClick()
    // AlertDialog content renders in a separate Popup window; give it an explicit beat to attach
    // rather than assuming performClick()'s built-in idle-wait already covers the new window.
    waitUntilTextExists(str(R.string.title_confirm_delete_question_dialog))
    if (hasAnswers && clearAnswers) {
        onNodeWithTag("deleteQuestionCheckbox").performClick()
    }
    onNodeWithText(str(R.string.btn_delete)).performClick()
    waitUntilGone("questionCard_$question")
}

fun ComposeRule.hideQuestion(question: String) {
    scrollToQuestionIcon("questionHide_$question")
    onNodeWithTag("questionHide_$question").performClick()
    waitUntilTextExists(str(R.string.title_confirm_hide_question_dialog))
    onNodeWithText(str(R.string.btn_hide)).performClick()
    waitUntilGone("questionCard_$question")
}

fun ComposeRule.openLogByQuestion(question: String) {
    scrollToQuestionIcon("questionLog_$question")
    onNodeWithTag("questionLog_$question").performClick()
    waitUntilTextExists(str(R.string.title_answer_log_activity))
}

fun ComposeRule.openAnswerScreen(question: String) {
    scrollToQuestion(question)
    onNodeWithTag("questionCard_$question").performClick()
}

// ---- Answer screen ----

fun ComposeRule.answerQuestion(question: String, answer: String) {
    openAnswerScreen(question)
    onNodeWithTag("answerTextField").performScrollTo().performTextInput(answer)
    onNodeWithText(str(R.string.btn_save)).performClick()
    waitUntilBackOnQuestionsScreen()
}

fun ComposeRule.answerBuiltInQuestion(questionTextRes: Int, answer: String) {
    answerQuestion(str(questionTextRes), answer)
}

fun ComposeRule.answerFeelings(groupLabelRes: Int, feeling: String) {
    openAnswerScreen(str(R.string.q_text_feelings))
    // The feelings dictionary lives in a plain verticalScroll Column (not a lazy list), so a node
    // outside the current viewport still "exists" for onNodeWith* but a click on it silently
    // lands off-screen — each target must be scrolled into view immediately before clicking it.
    onNodeWithTag("feelingsGroup_${str(groupLabelRes)}").performScrollTo().performClick()
    onNodeWithTag("feelingWord_$feeling").performScrollTo().performClick()
    onNodeWithText(str(R.string.btn_save)).performClick()
    waitUntilBackOnQuestionsScreen()
}

/** Waits until navigation back to the Questions screen (its hamburger icon) has landed. */
fun ComposeRule.waitUntilBackOnQuestionsScreen(timeoutMillis: Long = 5000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithContentDescription(str(R.string.cd_open_drawer)).fetchSemanticsNodes().isNotEmpty()
    }
}

fun ComposeRule.answerFeelingsRandom(): String {
    val feeling = activity.resources.getStringArray(R.array.anger_array)[0]
    answerFeelings(R.string.anger, feeling)
    return feeling
}

// ---- Log screens ----

fun ComposeRule.openFullLog() {
    onNodeWithContentDescription(str(R.string.cd_open_drawer)).performClick()
    onNodeWithText(str(R.string.nav_drawer_item_full_log)).performClick()
    waitUntilTextExists(str(R.string.title_full_log_activity))
}

fun ComposeRule.checkLastAnswerInLog(answer: String) {
    waitUntilTextExists(answer)
    onNodeWithText(answer).assertExists()
}

fun ComposeRule.checkNoAnswerInLog(answer: String) {
    onNodeWithText(answer).assertDoesNotExist()
}

private fun ComposeRule.openLogOverflowMenu() {
    waitUntil(5000) { onAllNodesWithContentDescription(str(R.string.cd_more)).fetchSemanticsNodes().isNotEmpty() }
    onNodeWithContentDescription(str(R.string.cd_more)).performClick()
}

fun ComposeRule.clearLog() {
    openLogOverflowMenu()
    onNodeWithText(str(R.string.btn_clear_log)).performClick()
    waitUntilTextExists(str(R.string.btn_clear))
    onNodeWithText(str(R.string.btn_clear)).performClick()
}

fun ComposeRule.clearDeletedAnswers() {
    openLogOverflowMenu()
    onNodeWithText(str(R.string.btn_clear_log_deleted)).performClick()
    waitUntilTextExists(str(R.string.btn_clear))
    onNodeWithText(str(R.string.btn_clear)).performClick()
}

fun ComposeRule.scrollToAnswerRow(answer: String) = scrollToLogIcon("deleteAnswer_$answer")

private fun ComposeRule.scrollToLogIcon(tag: String, timeoutMillis: Long = 8000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    var lastError: Throwable? = null
    while (System.currentTimeMillis() < deadline) {
        try {
            onNodeWithTag("logRowsList").performScrollToNode(hasTestTag(tag))
            return
        } catch (e: Throwable) {
            lastError = e
            Thread.sleep(200)
        }
    }
    throw lastError ?: AssertionError("Timed out waiting for log icon: $tag")
}

fun ComposeRule.openEditAnswer(answer: String) {
    scrollToLogIcon("editAnswer_$answer")
    onNodeWithTag("editAnswer_$answer").performClick()
}

fun ComposeRule.editAnswer(old: String, new: String) {
    openEditAnswer(old)
    onNodeWithTag("answerTextField").performScrollTo().performTextReplacement(new)
    onNodeWithText(str(R.string.btn_save)).performClick()
}

fun ComposeRule.deleteAnswer(answer: String) {
    scrollToAnswerRow(answer)
    onNodeWithTag("deleteAnswer_$answer").performClick()
}

fun ComposeRule.undoAnswerDeletion() {
    onNodeWithText(str(R.string.snackbar_undo)).performClick()
}

/** First (i.e. newest, since the log is newest-first) row's formatted date/time text. */
fun ComposeRule.firstLogRowDateTime(): String = textOfFirstWithTag("logRowDateTime")

fun ComposeRule.textOfTag(tag: String): String =
    onNodeWithTag(tag).fetchSemanticsNode().config
        .getOrElse(androidx.compose.ui.semantics.SemanticsProperties.Text) { emptyList() }
        .joinToString { it.text }

fun ComposeRule.textOfFirstWithTag(tag: String): String =
    onAllNodesWithTag(tag)[0].fetchSemanticsNode().config
        .getOrElse(androidx.compose.ui.semantics.SemanticsProperties.Text) { emptyList() }
        .joinToString { it.text }

// ---- Settings ----

fun ComposeRule.openSettings() {
    onNodeWithContentDescription(str(R.string.cd_open_drawer)).performClick()
    onNodeWithText(str(R.string.nav_drawer_item_settings)).performClick()
    waitUntilTextExists(str(R.string.title_settings_activity))
}

fun ComposeRule.restoreBuiltInQuestions() {
    openSettings()
    onNodeWithTag("restoreTriggerButton").performClick()
    waitUntilExists("restoreConfirmButton")
    onNodeWithTag("restoreConfirmButton").performClick()
    waitUntilTextExists(str(R.string.settings_built_in_all_visible))
    pressBack()
    waitUntilBackOnQuestionsScreen()
}

fun ComposeRule.changeDateAndTimeFormat(dateFormat: String, timeFormat: String) {
    openSettings()
    onNodeWithTag("dateFormatDropdown").performClick()
    waitUntilExists("dateFormatDropdownItem_$dateFormat")
    onNodeWithTag("dateFormatDropdownItem_$dateFormat").performClick()
    onNodeWithTag("timeFormatDropdown").performClick()
    waitUntilExists("timeFormatDropdownItem_$timeFormat")
    onNodeWithTag("timeFormatDropdownItem_$timeFormat").performClick()
    pressBack()
    waitUntilBackOnQuestionsScreen()
}

// ---- Snackbars ----

fun ComposeRule.checkSnackbar(messageRes: Int) {
    waitUntilTextExists(str(messageRes))
    onNodeWithText(str(messageRes)).assertExists()
}

fun ComposeRule.checkNoSnackbar(messageRes: Int) {
    onNodeWithText(str(messageRes)).assertDoesNotExist()
}

// ---- Assertions on button state ----

fun ComposeRule.checkSaveDisabledInQuestionSheet() {
    onNodeWithTag("questionSheetSaveButton").assertIsNotEnabled()
}
