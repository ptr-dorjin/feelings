package feelings.helper.answer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import feelings.helper.db.DbHelper;

import static android.provider.BaseColumns._ID;
import static feelings.helper.answer.AnswerContract.COLUMN_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_QUESTION_ID;
import static feelings.helper.answer.AnswerContract.ANSWER_TABLE;
import static feelings.helper.util.DateTimeUtil.DB_FORMATTER;

public class AnswerStore {

    public static boolean saveAnswer(Context context, Answer answer) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION_ID, answer.getQuestionId());
            values.put(COLUMN_DATE_TIME, answer.getDateTime().format(DB_FORMATTER));
            values.put(COLUMN_ANSWER, answer.getAnswerText());

            long newRowId = db.insert(ANSWER_TABLE, null, values);
            return newRowId != -1;
        } finally {
            db.close();
        }
    }

    public static Cursor getAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_QUESTION_ID, COLUMN_DATE_TIME, COLUMN_ANSWER};
        String orderBy = COLUMN_DATE_TIME + " DESC";

        return db.query(ANSWER_TABLE, projection, null, null, null, null, orderBy);
    }

    public static Cursor getByQuestionId(Context context, long questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_QUESTION_ID, COLUMN_DATE_TIME, COLUMN_ANSWER};
        String orderBy = COLUMN_DATE_TIME + " DESC";

        String selection = COLUMN_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        return db.query(ANSWER_TABLE, projection, selection, selectionArgs, null, null, orderBy);
    }

    // for tests only
    static int deleteAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            return db.delete(ANSWER_TABLE, null, null);
        } finally {
            db.close();
        }
    }
}
