package feelings.guide.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import feelings.guide.R;
import feelings.guide.question.QuestionContract;

import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.guide.question.QuestionContract.COLUMN_IS_HIDDEN;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_CODE_MAP;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;

class UpgraderTo3 {

    private final Context context;

    UpgraderTo3(Context context) {
        this.context = context;
    }

    void upgrade(SQLiteDatabase db) {
        addIsHiddenColumn(db);

        deleteQuestionByCode(db, R.string.q_insincerity);
        deleteQuestionByCode(db, R.string.q_preach);
        deleteQuestionByCode(db, R.string.q_lie);
        deleteQuestionByCode(db, R.string.q_irresponsibility);

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

    private void addIsHiddenColumn(SQLiteDatabase db) {
        db.execSQL("alter table " + QUESTION_TABLE + " add " + COLUMN_IS_HIDDEN + " INTEGER default 0");

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_HIDDEN, 0);
        db.update(QUESTION_TABLE, values, null, null);
    }

    private void deleteQuestionByCode(SQLiteDatabase db, int code) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DELETED, true);

        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = {context.getString(code)};

        db.update(QUESTION_TABLE, values, selection, selectionArgs);
    }

    private void changeLanguage(Context context, SQLiteDatabase db, int code) {
        ContentValues values = new ContentValues();

        QuestionContract.QuestionCode questionCode = QUESTION_CODE_MAP.get(code);
        values.put(COLUMN_TEXT, context.getString(questionCode.getTextId()));
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.getDescriptionId()));

        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = { context.getString(code) };

        db.update(QUESTION_TABLE, values, selection, selectionArgs);
    }
}
