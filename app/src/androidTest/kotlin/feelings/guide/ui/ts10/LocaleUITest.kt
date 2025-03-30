package feelings.guide.ui.ts10

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import feelings.guide.R
import feelings.guide.ui.question.QuestionListActivity
import feelings.guide.ui.util.checkQuestion
import feelings.guide.ui.util.checkSnackbar
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


private val locales = listOf(
    arrayOf("Русский", "Журнал", "Что я сейчас чувствую?"),
    arrayOf("Srpski", "Dnevnik", "Šta sada osjećam?"),
    arrayOf("English", "Log", "What do I feel right now?"),
)

@RunWith(Parameterized::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class LocaleUITest(
    private val locale: String,
    private val menuItem: String,
    private val feelingsQuestion: String
) {
    private lateinit var context: Context

    @get:Rule
    var activityRule = ActivityTestRule(QuestionListActivity::class.java)

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun step1_changeLocale() {
        // when
        changeLocale(locale)
        // then
        // the message is displayed on different languages on different APIs:
        //  - on Android < 7 it's in the new language
        //  - on Android >= 7 its in the old language
        // todo fix
        // checkSnackbar(R.string.msg_change_language_restart)
    }

    @Test
    fun step2_afterRestart_localeIsChanged() {
        // then
        onView(withId(R.id.show_log)).check(matches(withText(menuItem)))
        checkQuestion(feelingsQuestion)
        checkLocaleIsSaved()
    }

    private fun changeLocale(localeTitle: String) {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.btn_settings)).perform(click())
        onView(withText(R.string.title_settings_language)).perform(click())
        onView(withText(localeTitle)).perform(click())
        pressBack()
    }

    private fun checkLocaleIsSaved() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.btn_settings)).perform(click())
        onView(withText(R.string.title_settings_language)).perform(click())
        onView(withText(locale)).check(matches(isChecked()))
    }

    companion object {
        @JvmStatic
        @Parameters(name = "{index}: should be in {0}")
        fun data(): Iterable<Array<String>> = locales
    }
}