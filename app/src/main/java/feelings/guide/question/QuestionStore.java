package feelings.guide.question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

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

    private static final String TAG = QuestionStore.class.getSimpleName();

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

    /**
     * Only for user's question
     */
    static long createQuestion(Context context, Question question) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());
            values.put(COLUMN_IS_USER, question.isUser());
            values.put(COLUMN_IS_DELETED, false);
            values.put(COLUMN_IS_HIDDEN, false);

            return db.insert(QUESTION_TABLE, null, values);
        }
    }

    /**
     * Only for user's question
     */
    static boolean updateQuestion(Context context, Question question) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());

            String selection = _ID + " = ?";
            String[] selectionArgs = {valueOf(question.getId())};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        }
    }

    /**
     * Actually doesn't delete, but marks question as deleted
     * Only for user's question
     */
    static boolean deleteQuestion(Context context, long questionId) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_DELETED, true);

            String selection = _ID + " = ? AND " + COLUMN_IS_USER + " = ?";
            String[] selectionArgs = {valueOf(questionId), valueOf(1)};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        }
    }

    /**
     * Only for built-in questions
     */
    static boolean hideQuestion(Context context, long questionId) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_HIDDEN, true);

            String selection = _ID + " = ? AND " + COLUMN_IS_USER + " = ?";
            String[] selectionArgs = {valueOf(questionId), valueOf(0)};
            int count = db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
            return count == 1;
        }
    }

    /**
     * Only for built-in questions
     */
    static void restoreHidden(Context context) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_HIDDEN, false);

            String selection = COLUMN_IS_USER + " = ?";
            String[] selectionArgs = {valueOf(0)};
            db.update(QuestionContract.QUESTION_TABLE, values, selection, selectionArgs);
        }
    }

    static Cursor getAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String orderBy = _ID + " ASC";

        String selection = COLUMN_IS_DELETED + " = ? and " + COLUMN_IS_HIDDEN + " = ?";
        String[] selectionArgs = {valueOf(0), valueOf(0)};

        return db.query(QUESTION_TABLE, ALL_COLUMNS, selection, selectionArgs, null, null, orderBy);
    }

    /**
     * Change language of built-in questions
     */
    static void changeLanguage(Context context) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            changeLanguage(context, db, R.string.q_feelings);
            changeLanguage(context, db, R.string.q_gratitude);
            changeLanguage(context, db, R.string.q_do_body);
            changeLanguage(context, db, R.string.q_do_close);
            changeLanguage(context, db, R.string.q_do_others);
            changeLanguage(context, db, R.string.q_insincerity);
            changeLanguage(context, db, R.string.q_preach);
            changeLanguage(context, db, R.string.q_lie);
            changeLanguage(context, db, R.string.q_irresponsibility);
        }
    }

    private static void changeLanguage(Context context, SQLiteDatabase db, int code) {
        ContentValues values = new ContentValues();

        QuestionContract.QuestionCode questionCode = QUESTION_CODE_MAP.get(code);
        if (questionCode == null) {
            Log.w(TAG, "Could not find questionCode by code " + code);
            return;
        }
        values.put(COLUMN_TEXT, context.getString(questionCode.getTextId()));
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.getDescriptionId()));

        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = { context.getString(code) };

        db.update(QUESTION_TABLE, values, selection, selectionArgs);
    }
}
