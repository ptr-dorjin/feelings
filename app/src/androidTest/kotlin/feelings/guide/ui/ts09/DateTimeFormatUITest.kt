package feelings.guide.ui.ts09

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionsActivity
import feelings.guide.ui.util.first
import feelings.guide.ui.util.openFullLog
import feelings.guide.ui.util.openLogByQuestion
import org.hamcrest.Matchers.matchesPattern
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

private val dateFormats = mapOf(
    "d MMM yyyy" to "\\d{1,2} \\p{L}{3} \\d{4}",
    "MMM d yyyy" to "\\p{L}{3} \\d{1,2} \\d{4}",
    "dd.MM.yyyy" to "\\d{2}\\.\\d{2}\\.\\d{4}",
    "MM/dd/yyyy" to "\\d{2}/\\d{2}/\\d{4}",
    "yyyy-MM-dd" to "\\d{4}-\\d{2}-\\d{2}"
)
private val timeFormats = mapOf(
    "HH:mm" to "\\d{2}:\\d{2}",
    "hh:mm a" to "\\d{2}:\\d{2} [\\p{L} ]*",
    "h:mm a" to "\\d{1,2}:\\d{2} [\\p{L} ]*"
)

@RunWith(Parameterized::class)
@LargeTest
class DateTimeFormatUITest(private val dateFormat: String, private val timeFormat: String) {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun dateTimeFormatIsChanged_fullLogGetsUpdated() {
        // when
        changeDateAndTimeFormat()

        // then
        pressBack()
        openFullLog()
        val dateTimePattern = "${dateFormats[dateFormat]}, ${timeFormats[timeFormat]}"
        onView(first(withId(R.id.answerLogDateTime)))
            .check(matches(withText(matchesPattern(dateTimePattern))))
    }

    @Test
    fun dateTimeFormatIsChanged_questionLogGetsUpdated() {
        // when
        changeDateAndTimeFormat()

        // then
        pressBack()
        openLogByQuestion(R.string.q_text_feelings)
        val dateTimePattern = "${dateFormats[dateFormat]}, ${timeFormats[timeFormat]}"
        onView(first(withId(R.id.answerLogDateTime)))
            .check(matches(withText(matchesPattern(dateTimePattern))))
    }

    private fun changeDateAndTimeFormat() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.btn_settings)).perform(click())
        onView(withText(R.string.title_settings_date_format)).perform(click())
        onView(withText(dateFormat)).perform(click())
        onView(withText(R.string.title_settings_time_format)).perform(click())
        onView(withText(timeFormat)).perform(click())
    }

    companion object {
        @JvmStatic
        @Parameters(name = "{index}: should match pattern {0}, {1}")
        fun data(): Iterable<Array<String>> {
            val data = LinkedHashSet<Array<String>>()
            dateFormats.keys.forEach { dateFormat ->
                data.add(arrayOf(dateFormat, timeFormats.keys.first()))
            }
            timeFormats.keys.toList().subList(1, timeFormats.size).forEach { timeFormat ->
                data.add(arrayOf(dateFormats.keys.first(), timeFormat))
            }
            return data
        }
    }
}