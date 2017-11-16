package feelings.guide.question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import feelings.guide.db.DbHelper;

import static android.provider.BaseColumns._ID;
import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.guide.question.QuestionContract.COLUMN_IS_USER;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;

class QuestionStore {

    static Question getById(Context context, long questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_CODE, COLUMN_TEXT, COLUMN_DESCRIPTION, COLUMN_IS_USER, COLUMN_IS_DELETED};

        String selection = _ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(QUESTION_TABLE, projection, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                boolean isUser = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER));
                boolean isDeleted = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DELETED));
                return new Question(questionId, code, text, description, isUser, isDeleted);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    static long createQuestion(Context context, Question question) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());
            values.put(COLUMN_IS_USER, question.isUser());
            values.put(COLUMN_IS_DELETED, false);

            return db.insert(QUESTION_TABLE, null, values);
        } finally {
            db.close();
        }
    }

    static boolean updateQuestion(Context context, Question question) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());

            String selection = _ID + " = ?";
            String[] selectionArgs = {String.valueOf(question.getId())};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        } finally {
            db.close();
        }
    }

    /**
     * Actually doesn't delete, but marks question as deleted
     */
    static boolean deleteQuestion(Context context, long questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_DELETED, true);

            String selection = _ID + " = ?";
            String[] selectionArgs = {String.valueOf(questionId)};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        } finally {
            db.close();
        }
    }

    static Cursor getAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] projection = {_ID, COLUMN_TEXT, COLUMN_IS_USER, COLUMN_IS_DELETED};
        String orderBy = _ID + " ASC";

        String selection = COLUMN_IS_DELETED + " = ?";
        String[] selectionArgs = {String.valueOf(0)};

        return db.query(QUESTION_TABLE, projection, selection, selectionArgs, null, null, orderBy);
    }
}
