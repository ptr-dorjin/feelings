package feelings.helper.answer;

import android.provider.BaseColumns;

public class AnswerContract implements BaseColumns {
    static final String ANSWER_TABLE = "answer";
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_DATE_TIME = "date_time";
    public static final String COLUMN_ANSWER = "answer";

    public static final String SQL_CREATE_ANSWER_TABLE = "CREATE TABLE " + ANSWER_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_QUESTION_ID + " INTEGER," +
            COLUMN_DATE_TIME + " TEXT," +
            COLUMN_ANSWER + " TEXT)";

    private AnswerContract() {
    }
}
