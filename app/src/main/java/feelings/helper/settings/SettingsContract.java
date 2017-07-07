package feelings.helper.settings;

import android.provider.BaseColumns;

final class SettingsContract implements BaseColumns {
    static final String TABLE_NAME = "settings";
    static final String COLUMN_NAME_QUESTION_ID = "question_id";
    static final String COLUMN_NAME_IS_ON = "is_on";
    static final String COLUMN_NAME_REP_TYPE = "rep_type";
    static final String COLUMN_NAME_REPETITION = "repetition";

    private SettingsContract() {
    }
}
