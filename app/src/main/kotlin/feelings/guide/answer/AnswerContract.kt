package feelings.guide.answer

import android.provider.BaseColumns

internal const val ANSWER_TABLE = "answer"
internal const val COLUMN_QUESTION_ID = "question_id"
internal const val COLUMN_DATE_TIME = "date_time"
internal const val COLUMN_ANSWER = "answer"

internal val ALL_COLUMNS = arrayOf(
        BaseColumns._ID,
        COLUMN_QUESTION_ID,
        COLUMN_DATE_TIME,
        COLUMN_ANSWER
)

const val SQL_CREATE_ANSWER_TABLE = """CREATE TABLE $ANSWER_TABLE (
    ${BaseColumns._ID} INTEGER PRIMARY KEY,
    $COLUMN_QUESTION_ID INTEGER,
    $COLUMN_DATE_TIME TEXT,
    $COLUMN_ANSWER TEXT
)"""
