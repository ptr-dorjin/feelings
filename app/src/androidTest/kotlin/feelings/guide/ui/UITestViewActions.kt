package feelings.guide.ui

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap

fun clickXY(x: Int, y: Int): ViewAction {
    return GeneralClickAction(
        Tap.SINGLE,
        CoordinatesProvider { view ->
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