package feelings.helper.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import feelings.helper.repetition.RepetitionFactory;

import static feelings.helper.settings.SettingsContract.COLUMN_NAME_IS_ON;
import static feelings.helper.settings.SettingsContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.settings.SettingsContract.COLUMN_NAME_REPETITION;
import static feelings.helper.settings.SettingsContract.COLUMN_NAME_REP_TYPE;
import static feelings.helper.settings.SettingsContract.TABLE_NAME;

public class SettingsStore {

    /**
     * Create or update
     */
    public static boolean saveSettings(Context context, Settings settings) {
        SQLiteDatabase db = new SettingsDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_QUESTION_ID, settings.getQuestionId());
        values.put(COLUMN_NAME_IS_ON, settings.isOn());
        values.put(COLUMN_NAME_REP_TYPE, settings.getRepetition().getType());
        values.put(COLUMN_NAME_REPETITION, settings.getRepetition().toString());

        if (!exists(context, settings.getQuestionId())) {
            // create a new
            long newRowId = db.insert(TABLE_NAME, null, values);
            return newRowId != -1;
        } else {
            // update the existing
            String selection = COLUMN_NAME_QUESTION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(settings.getQuestionId())};
            int count = db.update(SettingsContract.TABLE_NAME, values, selection, selectionArgs);
            return count == 1;
        }
    }

    /**
     * Create or update only on/off flag
     */
    public static boolean switchOnOff(Context context, int questionId, boolean isOn) {
        SQLiteDatabase db = new SettingsDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_QUESTION_ID, questionId);
        values.put(COLUMN_NAME_IS_ON, isOn);

        if (exists(context, questionId)) {
            // update the existing
            String selection = COLUMN_NAME_QUESTION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(questionId)};
            int count = db.update(SettingsContract.TABLE_NAME, values, selection, selectionArgs);
            return count == 1;
        } else {
            throw new RuntimeException("Attempt to switch repetition on/off in the DB, while repetition not exists.");
        }
    }

    private static boolean exists(Context context, int questionId) {
        SQLiteDatabase db = new SettingsDbHelper(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID};

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            return cursor.moveToFirst();
        } finally { if (cursor != null) cursor.close(); }
    }

    public static Settings getSettings(Context context, int questionId) {
        SQLiteDatabase db = new SettingsDbHelper(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID, COLUMN_NAME_IS_ON, COLUMN_NAME_REP_TYPE, COLUMN_NAME_REPETITION};

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                boolean isOn = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_ON));
                String repType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REP_TYPE));
                String repetition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPETITION));
                return new Settings(questionId, isOn, RepetitionFactory.create(repType, repetition));
            } else {
                return null;
            }
        } finally { if (cursor != null) cursor.close(); }
    }

    // for use in tests
    static int delete(Context context, int questionId) {
        SQLiteDatabase db = new SettingsDbHelper(context).getWritableDatabase();

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    public static Collection<Settings> getAllSettings(Context context) {
        SQLiteDatabase db = new SettingsDbHelper(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID, COLUMN_NAME_IS_ON, COLUMN_NAME_REP_TYPE, COLUMN_NAME_REPETITION};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);

            List<Settings> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
                boolean isOn = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_ON));
                String repType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REP_TYPE));
                String repetition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPETITION));
                list.add(new Settings(questionId, isOn, RepetitionFactory.create(repType, repetition)));
            }
            return list;
        } finally { if (cursor != null) cursor.close(); }
    }
}
