package feelings.guide.question;

import android.provider.BaseColumns;

public class QuestionContract implements BaseColumns {
    public static final String QUESTION_TABLE = "question";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IS_USER = "is_user";
    public static final String COLUMN_IS_DELETED = "is_deleted";

    public static final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " + QUESTION_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            COLUMN_TEXT + " TEXT," +
            COLUMN_IS_USER + " INTEGER," +
            COLUMN_IS_DELETED + " INTEGER" +
            ")";

    private QuestionContract() {
    }
}
