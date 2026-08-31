package feelings.guide.ui.ts09

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import feelings.guide.R
import feelings.guide.ui.MainActivity
import feelings.guide.ui.answerFeelingsRandom
import feelings.guide.ui.changeDateAndTimeFormat
import feelings.guide.ui.firstLogRowDateTime
import feelings.guide.ui.openFullLog
import feelings.guide.ui.openLogByQuestion
import feelings.guide.ui.waitUntilTextExists
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.LinkedHashSet

private val dateFormats = mapOf(
    "d MMM yyyy" to Regex("\\d{1,2} \\p{L}{3,4} \\d{4}"),
    "MMM d yyyy" to Regex("\\p{L}{3,4} \\d{1,2} \\d{4}"),
    "dd.MM.yyyy" to Regex("\\d{2}\\.\\d{2}\\.\\d{4}"),
    "MM/dd/yyyy" to Regex("\\d{2}/\\d{2}/\\d{4}"),
    "yyyy-MM-dd" to Regex("\\d{4}-\\d{2}-\\d{2}"),
)
private val timeFormats = mapOf(
    "HH:mm" to Regex("\\d{2}:\\d{2}"),
    "hh:mm a" to Regex("\\d{2}:\\d{2} ?[\\p{L}.]*"),
    "h:mm a" to Regex("\\d{1,2}:\\d{2} ?[\\p{L}.]*"),
)

/**
 * @HiltAndroidTest doesn't compose well with JUnit's @Parameterized constructor-injection runner
 * via BaseComposeUiTest's field-rule pattern, so this test wires the same two rules directly.
 */
@HiltAndroidTest
@RunWith(Parameterized::class)
class DateTimeFormatUITest(private val dateFormat: String, private val timeFormat: String) {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.answerFeelingsRandom()
    }

    @Test
    fun dateTimeFormatIsChanged_fullLogGetsUpdated() {
        composeRule.changeDateAndTimeFormat(dateFormat, timeFormat)

        composeRule.openFullLog()
        composeRule.waitUntilTextExists(composeRule.activity.getString(R.string.title_full_log_activity))

        val actual = composeRule.firstLogRowDateTime()
        val expected = "${dateFormats.getValue(dateFormat).pattern} · ${timeFormats.getValue(timeFormat).pattern}"
        assertThat(actual).matches(expected)
    }

    @Test
    fun dateTimeFormatIsChanged_questionLogGetsUpdated() {
        composeRule.changeDateAndTimeFormat(dateFormat, timeFormat)

        composeRule.openLogByQuestion(composeRule.activity.getString(R.string.q_text_feelings))
        composeRule.waitUntilTextExists(composeRule.activity.getString(R.string.title_answer_log_activity))

        val actual = composeRule.firstLogRowDateTime()
        val expected = "${dateFormats.getValue(dateFormat).pattern} · ${timeFormats.getValue(timeFormat).pattern}"
        assertThat(actual).matches(expected)
    }

    companion object {
        @JvmStatic
        @Parameters(name = "{index}: should match pattern {0}, {1}")
        fun data(): Iterable<Array<String>> {
            val data = LinkedHashSet<Array<String>>()
            dateFormats.keys.forEach { dateFormat -> data.add(arrayOf(dateFormat, timeFormats.keys.first())) }
            timeFormats.keys.toList().subList(1, timeFormats.size).forEach { timeFormat ->
                data.add(arrayOf(dateFormats.keys.first(), timeFormat))
            }
            return data
        }
    }
}
