package feelings.helper.schedule;

import android.provider.BaseColumns;

public final class ScheduleContract implements BaseColumns {
    static final String TABLE_NAME = "schedule";
    static final String COLUMN_NAME_QUESTION_ID = "question_id";
    static final String COLUMN_NAME_IS_ON = "is_on";
    static final String COLUMN_NAME_REP_TYPE = "rep_type";
    static final String COLUMN_NAME_REPEAT = "repeat";

    public static final String SQL_CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_QUESTION_ID + " INTEGER," +
            COLUMN_NAME_IS_ON + " INTEGER," +
            COLUMN_NAME_REP_TYPE + " TEXT," +
            COLUMN_NAME_REPEAT + " TEXT)";

    private ScheduleContract() {
    }
}
