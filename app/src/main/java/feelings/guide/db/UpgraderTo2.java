package feelings.guide.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import feelings.guide.R;
import feelings.guide.question.QuestionContract;

import static android.provider.BaseColumns._ID;
import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_CODE_MAP;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;

class UpgraderTo2 {

    private final Context context;

    UpgraderTo2(Context context) {
        this.context = context;
    }

    void upgrade(SQLiteDatabase db) {
        db.execSQL("alter table " + QUESTION_TABLE + " add " + COLUMN_CODE + " TEXT");
        db.execSQL("alter table " + QUESTION_TABLE + " add " + COLUMN_DESCRIPTION + " TEXT");
        updateQuestionById(db, 1, R.string.q_feelings);
        updateQuestionById(db, 2, R.string.q_insincerity);
        updateQuestionById(db, 3, R.string.q_gratitude);
        updateQuestionById(db, 4, R.string.q_preach);
        updateQuestionById(db, 5, R.string.q_lie);
        updateQuestionById(db, 6, R.string.q_irresponsibility);
        updateQuestionById(db, 7, R.string.q_do_body);
        updateQuestionById(db, 8, R.string.q_do_close);
        updateQuestionById(db, 9, R.string.q_do_others);
    }

    private void updateQuestionById(SQLiteDatabase db, int questionId, int code) {
        ContentValues values = new ContentValues();

        QuestionContract.QuestionCode questionCode = QUESTION_CODE_MAP.get(code);
        values.put(COLUMN_CODE, context.getString(code));
        values.put(COLUMN_TEXT, context.getString(questionCode.getTextId()));
        values.put(COLUMN_DESCRIPTION, context.getString(questionCode.getDescriptionId()));

        String selection = _ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        db.update(QUESTION_TABLE, values, selection, selectionArgs);
    }
}
