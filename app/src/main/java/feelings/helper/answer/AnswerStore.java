package feelings.helper.answer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import feelings.helper.db.DbHelper;

import static android.provider.BaseColumns._ID;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.answer.AnswerContract.TABLE_NAME;
import static feelings.helper.util.DateTimeUtil.DB_FORMATTER;

public class AnswerStore {

    public static boolean saveAnswer(Context context, Answer answer) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_QUESTION_ID, answer.getQuestionId());
        values.put(COLUMN_NAME_DATE_TIME, answer.getDateTime().format(DB_FORMATTER));
        values.put(COLUMN_NAME_ANSWER, answer.getAnswerText());

        long newRowId = db.insert(TABLE_NAME, null, values);
        return newRowId != -1;
    }

    public static List<Answer> getAllAnswers(Context context) {
        Cursor cursor = null;
        try {
            cursor = getCursor(context);

            List<Answer> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
                LocalDateTime dateTime = LocalDateTime.parse(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE_TIME)),
                        DB_FORMATTER);
                String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ANSWER));
                list.add(new Answer(questionId, dateTime, answerText));
            }
            return list;
        } finally { if (cursor != null) cursor.close(); }
    }

    public static Cursor getCursor(Context context) {
        SQLiteDatabase db = new DbHelper(context).getReadableDatabase();

        String[] projection = {_ID, COLUMN_NAME_QUESTION_ID, COLUMN_NAME_DATE_TIME, COLUMN_NAME_ANSWER};
        String orderBy = COLUMN_NAME_DATE_TIME + " DESC";

        return db.query(TABLE_NAME, projection, null, null, null, null, orderBy);
    }

    static int deleteAll(Context context) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }
}
