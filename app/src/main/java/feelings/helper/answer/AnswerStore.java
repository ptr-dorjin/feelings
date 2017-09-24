package feelings.helper.answer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import feelings.helper.db.DbHelper;

import static android.provider.BaseColumns._ID;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.answer.AnswerContract.TABLE_NAME;
import static feelings.helper.util.DateTimeUtil.DB_FORMATTER;

public class AnswerStore {

    public static boolean saveAnswer(Context context, Answer answer) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_QUESTION_ID, answer.getQuestionId());
            values.put(COLUMN_NAME_DATE_TIME, answer.getDateTime().format(DB_FORMATTER));
            values.put(COLUMN_NAME_ANSWER, answer.getAnswerText());

            long newRowId = db.insert(TABLE_NAME, null, values);
            return newRowId != -1;
        } finally {
            db.close();
        }
    }

    public static Cursor getAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_NAME_QUESTION_ID, COLUMN_NAME_DATE_TIME, COLUMN_NAME_ANSWER};
        String orderBy = COLUMN_NAME_DATE_TIME + " DESC";

        return db.query(TABLE_NAME, projection, null, null, null, null, orderBy);
    }

    public static Cursor getByQuestionId(Context context, int questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_NAME_QUESTION_ID, COLUMN_NAME_DATE_TIME, COLUMN_NAME_ANSWER};
        String orderBy = COLUMN_NAME_DATE_TIME + " DESC";

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
    }

    // for tests only
    static int deleteAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            return db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }
}
