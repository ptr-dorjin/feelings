package feelings.guide.answer;

import android.provider.BaseColumns;

public class AnswerContract implements BaseColumns {
    static final String ANSWER_TABLE = "answer";
    static final String COLUMN_QUESTION_ID = "question_id";
    static final String COLUMN_DATE_TIME = "date_time";
    static final String COLUMN_ANSWER = "answer";

    static final String[] ALL_COLUMNS = {_ID, COLUMN_QUESTION_ID, COLUMN_DATE_TIME, COLUMN_ANSWER};

    public static final String SQL_CREATE_ANSWER_TABLE = "CREATE TABLE " + ANSWER_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_QUESTION_ID + " INTEGER," +
            COLUMN_DATE_TIME + " TEXT," +
            COLUMN_ANSWER + " TEXT)";

    private AnswerContract() {
    }

}
