package feelings.guide.ui.util

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException


fun clickXY(x: Int, y: Int): ViewAction {
    return GeneralClickAction(
            Tap.SINGLE,
            { view ->
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                floatArrayOf(
                        (screenPos[0] + x).toFloat(),
                        (screenPos[1] + y).toFloat()
                )
            },
            Press.FINGER, 0, 0
    )
}

fun waitId(viewId: Int, millis: Long = 1000) = object : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return isRoot()
    }

    override fun getDescription(): String {
        return "wait for a view with id <$viewId> in $millis ms."
    }

    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadUntilIdle()
        val startTime = System.currentTimeMillis()
        val endTime = startTime + millis
        val viewMatcher = withId(viewId)

        do {
            for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                // found view with required ID
                if (viewMatcher.matches(child)) {
                    return
                }
            }

            uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() <= endTime)

        // timeout happened
        throw PerformException.Builder()
                .withActionDescription(description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(TimeoutException())
                .build()
    }
}