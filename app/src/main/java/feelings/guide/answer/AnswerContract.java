package feelings.guide.answer;

import android.database.Cursor;
import android.provider.BaseColumns;

import org.threeten.bp.LocalDateTime;

import static feelings.guide.util.DateTimeUtil.DB_FORMATTER;

public class AnswerContract implements BaseColumns {
    static final String ANSWER_TABLE = "answer";
    static final String COLUMN_QUESTION_ID = "question_id";
    static final String COLUMN_DATE_TIME = "date_time";
    static final String COLUMN_ANSWER = "answer";

    public static final String SQL_CREATE_ANSWER_TABLE = "CREATE TABLE " + ANSWER_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_QUESTION_ID + " INTEGER," +
            COLUMN_DATE_TIME + " TEXT," +
            COLUMN_ANSWER + " TEXT)";

    private AnswerContract() {
    }

    public static Answer mapFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        long questionId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID));
        LocalDateTime dateTime = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                DB_FORMATTER);
        String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));

        return new Answer(id, questionId, dateTime, answerText);
    }
}
