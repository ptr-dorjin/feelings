package feelings.guide.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import feelings.guide.profile.LocaleUtil

import android.content.pm.PackageManager.GET_META_DATA

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.setLocale(base))
    }

    // todo what is it for?
    private fun resetTitle() {
        try {
            val label = packageManager.getActivityInfo(componentName, GET_META_DATA).labelRes
            if (label != 0) {
                setTitle(label)
            }
        } catch (ignore: PackageManager.NameNotFoundException) {
            //do nothing
        }

    }
}
