package feelings.guide.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

/**
 * Base for Compose UI tests running against the Hilt test app, an in-memory Room DB
 * (TestDatabaseModule) and a scratch DataStore (TestDataStoreModule) — see QuestionListSmokeTest
 * for the minimal version of this harness.
 */
@HiltAndroidTest
abstract class BaseComposeUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUpHilt() {
        hiltRule.inject()
    }
}
