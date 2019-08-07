package feelings.guide.ui.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import feelings.guide.R
import feelings.guide.randomAlphanumericString
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not


internal fun answerFeelings(feelingsGroupId: Int, feeling: String, appendix: String = "") {
    onView(withText(R.string.q_text_feelings)).perform(click())
    onView(allOf(withId(R.id.labelFeelingsGroup), withText(feelingsGroupId))).perform(click())
    onView(allOf(withId(R.id.labelFeelingItem), withText(feeling))).perform(click())
    if (appendix.isNotEmpty())
        onView(withId(R.id.answerText)).perform(replaceText("$feeling $appendix"), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun answerFeelingsRandom(): String {
    val feeling = getApplicationContext<Context>().resources.getStringArray(R.array.anger_array)[0]
    val appendix = randomAlphanumericString()
    answerFeelings(R.string.anger, feeling, appendix)
    return "$feeling $appendix"
}

internal fun answerBuiltInQuestion(questionId: Int, answer: String) {
    val question = getApplicationContext<Context>().getString(questionId)
    answerQuestion(question, answer)
}

internal fun answerQuestion(question: String, answer: String) {
    scrollToQuestion(question)
    onView(withText(question)).perform(click())
    onView(withId(R.id.answerText)).perform(typeText(answer), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun checkLastAnswerInLogFull(answer: String) {
    onView(first(withId(R.id.logFullAnswer))).check(matches(withText(answer)))
}

internal fun checkLastAnswerInLogByQuestion(answer: String) {
    onView(first(withId(R.id.logByQuestionAnswer))).check(matches(withText(answer)))
}

internal fun checkNoAnswerInLogFull(answer: String) {
    onView(withId(R.id.logFullRV)).check(matches(not(hasItem(hasDescendant(withText(answer))))))
}

internal fun checkNoAnswerInLogByQuestion(answer: String) {
    onView(withId(R.id.logByQuestionRV)).check(matches(not(hasItem(hasDescendant(withText(answer))))))
}

internal fun openFullLog() {
    onView(withId(R.id.show_log)).perform(click())
}

/**
 * assumes there is only one question with this text, otherwise throws an exception
 */
internal fun scrollToQuestion(question: String) {
    onView(withId(R.id.questionRV)).perform(scrollToHolder(questionWithText(question)))
}

internal fun scrollToQuestion(questionId: Int) {
    val question = getApplicationContext<Context>().getString(questionId)
    scrollToQuestion(question)
}

internal fun openLogByQuestion(questionId: Int) {
    val question = getApplicationContext<Context>().getString(questionId)
    openLogByQuestion(question)
}

internal fun openLogByQuestion(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_show_log)).perform(click())
}

internal fun clearLogFromQuestionList(questionId: Int) {
    val question = getApplicationContext<Context>().getString(questionId)
    clearLogFromQuestionList(question)
}

internal fun clearLogFromQuestionList(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_clear_log)).perform(click())
    onView(withText(R.string.btn_delete)).perform(click())
}

internal fun clearLogFromQuestionLog() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    onView(withText(R.string.btn_clear_log)).perform(click())
    onView(withText(R.string.btn_delete)).perform(click())
}

internal fun clearLogFull() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    onView(withText(R.string.btn_clear_log_full)).perform(click())
    onView(withText(R.string.btn_delete)).perform(click())
}

internal fun clearLogHiddenDeleted() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    onView(withText(R.string.btn_clear_log_deleted)).perform(click())
    onView(withText(R.string.btn_delete)).perform(click())
}

internal fun hideQuestion(questionId: Int) {
    scrollToQuestion(questionId)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(questionId)))).perform(click())
    onView(withText(R.string.btn_hide)).perform(click()) // hide on popup
    onView(withText(R.string.btn_hide)).perform(click()) // hide on confirmation dialog
}

internal fun restoreBuiltInQuestions() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    onView(withText(R.string.btn_settings)).perform(click())
    onView(withText(R.string.title_settings_restore_built_in_questions)).perform(click())
    onView(withText(R.string.btn_restore)).perform(click())
}

internal fun addUserQuestion(question: String) {
    onView(withId(R.id.questionFab)).perform(click())
    onView(withId(R.id.questionTextEdit)).perform(typeText(question), closeSoftKeyboard())
    onView(withText(R.string.btn_save)).perform(click())
}

internal fun openEditQuestionDialog(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_edit)).perform(click())
}

internal fun editUserQuestion(old: String, new: String) {
    openEditQuestionDialog(old)
    onView(withId(R.id.questionTextEdit)).perform(replaceText(new), closeSoftKeyboard())
    onView(withText(R.string.btn_save)).perform(click())
}

internal fun deleteUserQuestion(question: String, hasAnswers: Boolean = false, clearAnswers: Boolean = true) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_delete)).perform(click()) // delete on popup
    onView(withText(R.string.btn_delete)).perform(click()) // delete on confirmation dialog for question
    if (hasAnswers) {
        if (clearAnswers)
            onView(withText(R.string.btn_delete)).perform(click()) // delete on confirmation dialog for its answers
        else
            onView(withText(R.string.btn_cancel)).perform(click())
    }
}

internal fun checkQuestion(questionId: Int) {
    val question = getApplicationContext<Context>().getString(questionId)
    checkQuestion(question)
}

internal fun checkQuestion(question: String) {
    onView(withId(R.id.questionRV))
        .check(matches(hasItem(hasDescendant(withText(question)))))
}

internal fun checkNoQuestion(questionId: Int) {
    val question = getApplicationContext<Context>().getString(questionId)
    checkNoQuestion(question)
}

internal fun checkNoQuestion(question: String) {
    onView(withId(R.id.questionRV))
        .check(matches(not(hasItem(hasDescendant(withText(question))))))
}

internal fun checkSnackbar(stringId: Int) {
    val snackbarId = com.google.android.material.R.id.snackbar_text
    onView(isRoot()).perform(waitId(snackbarId))
    onView(withId(snackbarId)).check(matches(withText(stringId)))
}

internal fun checkNoSnackbar(stringId: Int) {
    val snackbarId = com.google.android.material.R.id.snackbar_text
    onView(allOf(withId(snackbarId), withText(stringId))).check(doesNotExist())
}

fun waitForSnackbar() {
    onView(isRoot()).perform(waitId(com.google.android.material.R.id.snackbar_action))
}

internal fun openEditAnswerInLogFull(answer: String) {
    onView(withId(R.id.logFullRV))
        .perform(actionOnHolderItem(answerWithTextInLogFull(answer), swipeLeft()))
}

internal fun openEditAnswerInLogByQuestion(answer: String) {
    onView(withId(R.id.logByQuestionRV))
        .perform(actionOnHolderItem(answerWithTextInLogByQuestion(answer), swipeLeft()))
}

internal fun editAnswerInLogFull(old: String, new: String) {
    openEditAnswerInLogFull(old)
    onView(withId(R.id.answerText)).perform(replaceText(new), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun editAnswerInLogByQuestion(old: String, new: String) {
    openEditAnswerInLogByQuestion(old)
    onView(withId(R.id.answerText)).perform(replaceText(new), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun deleteAnswerInLogFull(answer: String) {
    onView(withId(R.id.logFullRV)).perform(
        actionOnHolderItem(
            answerWithTextInLogFull(answer), swipeRight()
        )
    )
}

internal fun deleteAnswerInLogByQuestion(answer: String) {
    onView(withId(R.id.logByQuestionRV)).perform(
        actionOnHolderItem(
            answerWithTextInLogByQuestion(answer), swipeRight()
        )
    )
}