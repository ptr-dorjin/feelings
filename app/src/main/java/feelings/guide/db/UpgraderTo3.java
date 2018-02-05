package feelings.guide.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import static feelings.guide.question.QuestionContract.COLUMN_IS_HIDDEN;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;

class UpgraderTo3 {

    UpgraderTo3() {
    }

    void upgrade(SQLiteDatabase db) {
        db.execSQL("alter table " + QUESTION_TABLE + " add " + COLUMN_IS_HIDDEN + " INTEGER default 0");

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_HIDDEN, 0);
        db.update(QUESTION_TABLE, values, null, null);
    }
}
