package feelings.guide.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import feelings.guide.ui.question.QuestionsAdapter
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

internal fun <T> first(matcher: Matcher<T>): Matcher<T> {
    return object : BaseMatcher<T>() {
        var isFirst = true

        override fun describeTo(description: Description) {
            description.appendText("has first item: ")
            matcher.describeTo(description)
        }

        override fun matches(item: Any): Boolean {
            if (isFirst && matcher.matches(item)) {
                isFirst = false
                return true
            }
            return false
        }
    }
}

internal fun questionWithText(text: String): Matcher<RecyclerView.ViewHolder> {
    return object : BoundedMatcher<RecyclerView.ViewHolder, QuestionsAdapter.QuestionViewHolder>(
        QuestionsAdapter.QuestionViewHolder::class.java
    ) {
        override fun describeTo(description: Description) {
            description.appendText("has a question with the specified text")
        }

        override fun matchesSafely(vh: QuestionsAdapter.QuestionViewHolder) = vh.questionText.text.toString() == text
    }
}

internal fun hasItem(matcher: Matcher<View>): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

        override fun describeTo(description: Description) {
            description.appendText("has item: ")
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean {
            val adapter = view.adapter
            for (position in 0 until adapter!!.itemCount) {
                val type = adapter.getItemViewType(position)
                val holder = adapter.createViewHolder(view, type)
                adapter.onBindViewHolder(holder, position)
                if (matcher.matches(holder.itemView)) {
                    return true
                }
            }
            return false
        }
    }
}