package feelings.helper.schedule;

import android.provider.BaseColumns;

final class ScheduleContract implements BaseColumns {
    static final String TABLE_NAME = "schedule";
    static final String COLUMN_NAME_QUESTION_ID = "question_id";
    static final String COLUMN_NAME_IS_ON = "is_on";
    static final String COLUMN_NAME_REP_TYPE = "rep_type";
    static final String COLUMN_NAME_REPEAT = "repeat";

    private ScheduleContract() {
    }
}
