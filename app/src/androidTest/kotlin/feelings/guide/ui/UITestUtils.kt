package feelings.guide.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import androidx.test.espresso.matcher.ViewMatchers.*
import feelings.guide.R
import org.hamcrest.Matchers.allOf


internal fun addFeelingsAnswer(feelingsGroupId: Int, feeling: String) {
    onView(withText(R.string.q_text_feelings)).perform(click())
    onView(allOf(withId(R.id.labelFeelingsGroup), withText(feelingsGroupId))).perform(click())
    onView(allOf(withId(R.id.labelFeelingItem), withText(feeling))).perform(click())
    onView(withId(R.id.save)).perform(click())
}

internal fun addBuiltInAnswer(answer: String, questionId: Int) {
    onView(withText(questionId)).perform(click())
    onView(withId(R.id.answerText)).perform(typeText(answer), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun addUserQuestion(question: String) {
    onView(withId(R.id.questionFab)).perform(click())
    onView(withId(R.id.questionTextEdit)).perform(typeText(question), closeSoftKeyboard())
    onView(withText(R.string.btn_save)).perform(click())
}

/**
 * assumes there is only one question with this text, otherwise throws an exception
 */
private fun scrollToQuestion(question: String) {
    onView(withId(R.id.questionRV)).perform(scrollToHolder(questionWithText(question)))
}

internal fun deleteUserQuestion(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_delete)).perform(click()) // delete on popup
    onView(withText(R.string.btn_delete)).perform(click()) // delete on confirmation dialog
}

internal fun addUserAnswer(question: String, answer: String) {
    scrollToQuestion(question)
    onView(withText(question)).perform(click())
    onView(withId(R.id.answerText)).perform(typeText(answer), closeSoftKeyboard())
    onView(withId(R.id.save)).perform(click())
}

internal fun openLogByQuestion(questionId: Int) {
    scrollToQuestion((getApplicationContext() as Context).getString(questionId))
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(questionId)))).perform(click())
    onView(withText(R.string.btn_show_log)).perform(click())
}

internal fun openLogByQuestion(question: String) {
    scrollToQuestion(question)
    onView(allOf(withId(R.id.popupMenu), hasSibling(withText(question)))).perform(click())
    onView(withText(R.string.btn_show_log)).perform(click())
}

internal fun openFullLog() {
    onView(withId(R.id.show_log)).perform(click())
}
