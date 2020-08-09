package feelings.guide.util

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.File

/**
 * starting from Android 10 (Q), no need to get permission when using MediaStore
 */
fun canWriteToExternalStorage(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                || ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

fun getExternalStorageMounted(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

fun setExternalStoragePath(contentValues: ContentValues, fileName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    } else {
        @Suppress("DEPRECATION")
        val externalStorageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        } else {
            Environment.getExternalStorageDirectory()
        }
        @Suppress("DEPRECATION")
        contentValues.put(MediaStore.MediaColumns.DATA,
                externalStorageDir.toString() + File.separator + fileName)
    }
}