package feelings.guide.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlakyTest(detail = "Fails on Android 5, 7, 8, 9. Passes on Android 4, 6")
@RunWith(AndroidJUnit4::class)
@LargeTest
class FeelingsListUITest {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionsActivity::class.java)

    @Before
    fun before() {
        context = getApplicationContext()
    }

    @Test
    fun selectFeelingWithScroll_closesAnswerActivity() {
        // given
        val feeling = context.resources.getStringArray(R.array.love_array)[4]

        // when
        addFeelingsAnswer(R.string.love, feeling)

        // then
        onView(withId(R.id.questionRV)).check(matches(isDisplayed()))
    }
}