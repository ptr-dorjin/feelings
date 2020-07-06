package feelings.guide.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("create temporary table question_backup(_id integer primary key autoincrement, code text, text text, is_user integer, is_deleted integer, is_hidden integer)")
        database.execSQL("insert into question_backup select _id, code, text, is_user, is_deleted, is_hidden from question")
        database.execSQL("drop table question")
        database.execSQL("create temporary table question(_id integer primary key autoincrement, code text, text text, is_user integer, is_deleted integer, is_hidden integer)")
        database.execSQL("insert into question select _id, code, text, is_user, is_deleted, is_hidden from question_backup")
        database.execSQL("drop table question_backup")
    }
}