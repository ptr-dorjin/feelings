package feelings.guide.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import feelings.guide.ui.question.QuestionsAdapter
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

internal fun <T> first(matcher: Matcher<T>): Matcher<T> {
    return object : BaseMatcher<T>() {
        var isFirst = true

        override fun matches(item: Any): Boolean {
            if (isFirst && matcher.matches(item)) {
                isFirst = false
                return true
            }
            return false
        }

        override fun describeTo(description: Description) {
            description.appendText("should return first matching item")
        }
    }
}

internal fun questionWithText(text: String): Matcher<RecyclerView.ViewHolder> {
    return object : BoundedMatcher<RecyclerView.ViewHolder, QuestionsAdapter.QuestionViewHolder>(
        QuestionsAdapter.QuestionViewHolder::class.java
    ) {
        override fun matchesSafely(vh: QuestionsAdapter.QuestionViewHolder) = vh.questionText.text.toString() == text

        override fun describeTo(description: Description) {
            description.appendText("should return a question with the specified text")
        }
    }
}