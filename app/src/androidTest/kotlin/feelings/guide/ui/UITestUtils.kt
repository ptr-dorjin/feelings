package feelings.guide.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import feelings.guide.R
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not


internal fun addFeelingsAnswer(feelingsGroupId: Int, feeling: String) {
    onView(withText(R.string.q_text_feelings)).perform(click())
    onView(allOf(withId(R.id.labelFeelingsGroup), withText(feelingsGroupId))).perform(click())
    onView(allOf(withId(R.id.labelFeelingItem), withText(feeling))).perform(click())
    onView(withId(R.id.save)).perform(click())
}

internal fun addBuiltInAnswer(questionId: Int, answer: String) {
    val question = (getApplicationContext() as Context).getString(questionId)
    addAnswer(question, answer)
}

internal fun addAnswer(question: String, answer: String) {
    scrollToQuestion(question)
    onView(withText(question)).perform(click())
    onView(withId(R.id.answerText)).perform(typeText(answer), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun checkLastAnswer(answer: String) {
    onView(first(withId(R.id.answerLogAnswer))).check(matches(withText(answer)))
}

internal fun checkNoAnswer(answer: String) {
    onView(withId(R.id.answerLogRV)).check(matches(not(hasItem(hasDescendant(withText(answer))))))
}

internal fun openFullLog() {
    onView(withId(R.id.show_log)).perform(click())
}

/**
 * assumes there is only one question with this text, otherwise throws an exception
 */
private fun scrollToQuestion(question: String) {
    onView(withId(R.id.questionRV)).perform(scrollToHolder(questionWithText(question)))
}

internal fun openLogByQuestion(questionId: Int) {
    val question = (getApplicationContext() as Context).getString(questionId)
    openLogByQuestion(question)
}

internal fun openLogByQuestion(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_show_log)).perform(click())
}

internal fun clearLogFromQuestionList(questionId: Int) {
    val question = (getApplicationContext() as Context).getString(questionId)
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
    scrollToQuestion((getApplicationContext() as Context).getString(questionId))
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

internal fun deleteUserQuestion(question: String, hasAnswers: Boolean = false) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_delete)).perform(click()) // delete on popup
    onView(withText(R.string.btn_delete)).perform(click()) // delete on confirmation dialog for question
    if (hasAnswers) onView(withText(R.string.btn_delete)).perform(click()) // delete on confirmation dialog for its answers
}

internal fun checkQuestion(questionId: Int) {
    val question = (getApplicationContext() as Context).getString(questionId)
    checkQuestion(question)
}

internal fun checkQuestion(question: String) {
    onView(withId(R.id.questionRV)).check(matches(hasItem(hasDescendant(withText(question)))))
}

internal fun checkNoQuestion(questionId: Int) {
    val question = (getApplicationContext() as Context).getString(questionId)
    checkNoQuestion(question)
}

internal fun checkNoQuestion(question: String) {
    onView(withId(R.id.questionRV)).check(matches(not(hasItem(hasDescendant(withText(question))))))
}

internal fun checkSnackbar(stringId: Int) {
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(stringId)))
}
