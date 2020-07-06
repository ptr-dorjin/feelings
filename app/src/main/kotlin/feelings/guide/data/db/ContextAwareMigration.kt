package feelings.guide.data.db

import android.content.Context
import androidx.room.migration.Migration

internal abstract class ContextAwareMigration(startVersion: Int, endVersion: Int) :
        Migration(startVersion, endVersion) {
    lateinit var context: Context
}