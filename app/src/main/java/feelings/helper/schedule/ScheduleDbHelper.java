package feelings.helper.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_IS_ON;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_REPEAT;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_REP_TYPE;
import static feelings.helper.schedule.ScheduleContract.TABLE_NAME;

class ScheduleDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeelingsHelper.db";

    private static final String SQL_CREATE_SCHEDULES = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_QUESTION_ID + " INTEGER," +
            COLUMN_NAME_IS_ON + " INTEGER," +
            COLUMN_NAME_REP_TYPE + " TEXT," +
            COLUMN_NAME_REPEAT + " TEXT)";

    ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SCHEDULES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // so far do nothing
    }
}