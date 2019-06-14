package feelings.guide.util

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object ToastUtil {

    fun showLong(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.show()
    }

    fun showShort(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.show()
    }

    /**
     * Needed on edit screens, where the keyboard is being rendered, in order not to overlap with the keyboard.
     */
    fun showShortTop(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 200)
        toast.show()
    }
}