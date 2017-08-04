package feelings.helper.answers;

import android.provider.BaseColumns;

public class AnswerContract implements BaseColumns {
    static final String TABLE_NAME = "answer";
    static final String COLUMN_NAME_QUESTION_ID = "question_id";
    static final String COLUMN_NAME_DATE_TIME = "date_time";
    static final String COLUMN_NAME_ANSWER = "answer";

    public static final String SQL_CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_QUESTION_ID + " INTEGER," +
            COLUMN_NAME_DATE_TIME + " TEXT," +
            COLUMN_NAME_ANSWER + " TEXT)";

    private AnswerContract() {
    }
}
