package feelings.guide.question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import feelings.guide.R;
import feelings.guide.db.DbHelper;

import static android.provider.BaseColumns._ID;
import static feelings.guide.question.QuestionContract.ALL_COLUMNS;
import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.guide.question.QuestionContract.COLUMN_IS_HIDDEN;
import static feelings.guide.question.QuestionContract.COLUMN_IS_USER;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_CODE_MAP;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;
import static java.lang.String.valueOf;

class QuestionStore {

    static Question getById(Context context, long questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        String selection = _ID + " = ?";
        String[] selectionArgs = {valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(QUESTION_TABLE, ALL_COLUMNS, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToQuestion(cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    @NonNull
    static Question cursorToQuestion(Cursor cursor) {
        Question question = new Question();
        question.setId(cursor.getLong(cursor.getColumnIndexOrThrow(_ID)));
        question.setCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)));
        question.setText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)));
        question.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        question.setUser(1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)));
        question.setDeleted(1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DELETED)));
        question.setHidden(1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_HIDDEN)));
        return question;
    }

    static long createQuestion(Context context, Question question) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());
            values.put(COLUMN_IS_USER, question.isUser());
            values.put(COLUMN_IS_DELETED, false);
            values.put(COLUMN_IS_HIDDEN, false);

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
            String[] selectionArgs = {valueOf(question.getId())};
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

            String selection = _ID + " = ? AND " + COLUMN_IS_USER + " = ?";
            String[] selectionArgs = {valueOf(questionId), valueOf(1)};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        } finally {
            db.close();
        }
    }

    static boolean hideSystemQuestion(Context context, long questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_HIDDEN, true);

            String selection = _ID + " = ? AND " + COLUMN_IS_USER + " = ?";
            String[] selectionArgs = {valueOf(questionId), valueOf(0)};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        } finally {
            db.close();
        }
    }

    static Cursor getAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String orderBy = _ID + " ASC";

        String selection = COLUMN_IS_DELETED + " = ? and " + COLUMN_IS_HIDDEN + " = ?";
        String[] selectionArgs = {valueOf(0), valueOf(0)};

        return db.query(QUESTION_TABLE, ALL_COLUMNS, selection, selectionArgs, null, null, orderBy);
    }

    static boolean changeLanguage(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            boolean result;
            result = changeLanguage(context, db, R.string.q_feelings);
            result = result && changeLanguage(context, db, R.string.q_insincerity);
            result = result && changeLanguage(context, db, R.string.q_gratitude);
            result = result && changeLanguage(context, db, R.string.q_preach);
            result = result && changeLanguage(context, db, R.string.q_lie);
            result = result && changeLanguage(context, db, R.string.q_irresponsibility);
            result = result && changeLanguage(context, db, R.string.q_do_body);
            result = result && changeLanguage(context, db, R.string.q_do_close);
            result = result && changeLanguage(context, db, R.string.q_do_others);
            return result;
        } finally {
            db.close();
        }
    }

    private static boolean changeLanguage(Context context, SQLiteDatabase db, int code) {
        ContentValues values = new ContentValues();

        QuestionContract.QuestionCode questionCode = QUESTION_CODE_MAP.get(code);
        values.put(COLUMN_TEXT, context.getString(questionCode.getTextId()));
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.getDescriptionId()));

        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = { context.getString(code) };

        int count = db.update(QUESTION_TABLE, values, selection, selectionArgs);
        return count == 1;
    }
}
