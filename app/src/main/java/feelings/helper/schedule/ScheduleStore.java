package feelings.helper.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import feelings.helper.db.DbHelper;
import feelings.helper.repeat.RepeatFactory;
import feelings.helper.repeat.RepeatType;

import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_IS_ON;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_QUESTION_ID;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_REPEAT;
import static feelings.helper.schedule.ScheduleContract.COLUMN_NAME_REP_TYPE;
import static feelings.helper.schedule.ScheduleContract.TABLE_NAME;

class ScheduleStore {
    private static final Logger log = LoggerFactory.getLogger(ScheduleStore.class);

    /**
     * Create or update
     */
    static boolean saveSchedule(Context context, Schedule schedule) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_QUESTION_ID, schedule.getQuestionId());
            values.put(COLUMN_NAME_IS_ON, schedule.isOn());
            values.put(COLUMN_NAME_REP_TYPE, schedule.getRepeatType().name());
            values.put(COLUMN_NAME_REPEAT, schedule.getRepeat().toDbString());

            //no need a transaction until only one table updated
            if (!exists(context, schedule.getQuestionId())) {
                // create a new
                long newRowId = db.insert(TABLE_NAME, null, values);
                return newRowId != -1;
            } else {
                // update the existing
                String selection = COLUMN_NAME_QUESTION_ID + " = ?";
                String[] selectionArgs = {String.valueOf(schedule.getQuestionId())};
                int count = db.update(ScheduleContract.TABLE_NAME, values, selection, selectionArgs);
                return count == 1;
            }
        } finally {
            db.close();
        }
    }

    /**
     * Create or update only on/off flag
     */
    static boolean switchOnOff(Context context, int questionId, boolean isOn) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_QUESTION_ID, questionId);
            values.put(COLUMN_NAME_IS_ON, isOn);

            if (exists(context, questionId)) {
                // update the existing
                String selection = COLUMN_NAME_QUESTION_ID + " = ?";
                String[] selectionArgs = {String.valueOf(questionId)};
                int count = db.update(ScheduleContract.TABLE_NAME, values, selection, selectionArgs);
                return count == 1;
            } else {
                log.error("Attempt to switch repeat on/off in the DB, while schedule not exists.");
                return false;
            }
        } finally {
            db.close();
        }
    }

    private static boolean exists(Context context, int questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID};

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            return cursor.moveToFirst();
        } finally { if (cursor != null) cursor.close(); }
        //db will be closed in calling methods
    }

    static Schedule getSchedule(Context context, int questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID, COLUMN_NAME_IS_ON, COLUMN_NAME_REP_TYPE, COLUMN_NAME_REPEAT};

        String selection = COLUMN_NAME_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                boolean isOn = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_ON));
                RepeatType repType = RepeatType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REP_TYPE)));
                String repeat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPEAT));
                return new Schedule(questionId, isOn, repType, RepeatFactory.create(repType, repeat));
            } else {
                return null;
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    static Collection<Schedule> getAllSchedules(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        String[] projection = {COLUMN_NAME_QUESTION_ID, COLUMN_NAME_IS_ON, COLUMN_NAME_REP_TYPE, COLUMN_NAME_REPEAT};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);

            List<Schedule> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
                boolean isOn = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_IS_ON));
                RepeatType repType = RepeatType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REP_TYPE)));
                String repeat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPEAT));
                list.add(new Schedule(questionId, isOn, repType, RepeatFactory.create(repType, repeat)));
            }
            return list;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // for use in tests
    static int delete(Context context, int questionId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            String selection = COLUMN_NAME_QUESTION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(questionId)};

            return db.delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            db.close();
        }
    }

    static int deleteAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        try {
            return db.delete(TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }
}
