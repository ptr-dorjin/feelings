package feelings.guide.question;

import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

import feelings.guide.R;

public class QuestionContract implements BaseColumns {
    public static final String QUESTION_TABLE = "question";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_USER = "is_user";
    public static final String COLUMN_IS_DELETED = "is_deleted";
    public static final String COLUMN_IS_HIDDEN = "is_hidden";

    static final String[] ALL_COLUMNS = {_ID, COLUMN_CODE, COLUMN_TEXT, COLUMN_DESCRIPTION,
            COLUMN_IS_USER, COLUMN_IS_DELETED, COLUMN_IS_HIDDEN};

    public static final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " + QUESTION_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_CODE + " TEXT, " +
            COLUMN_TEXT + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IS_USER + " INTEGER, " +
            COLUMN_IS_DELETED + " INTEGER, " +
            COLUMN_IS_HIDDEN + " INTEGER" +
            ")";

    public static final Map<Integer, QuestionCode> QUESTION_CODE_MAP = new HashMap<>();

    static {
        map(new QuestionCode(R.string.q_feelings, R.string.q_text_feelings, R.string.q_description_feelings));
        map(new QuestionCode(R.string.q_insincerity, R.string.q_text_insincerity, R.string.q_description_insincerity));
        map(new QuestionCode(R.string.q_gratitude, R.string.q_text_gratitude, R.string.q_description_gratitude));
        map(new QuestionCode(R.string.q_preach, R.string.q_text_preach, R.string.q_description_preach));
        map(new QuestionCode(R.string.q_lie, R.string.q_text_lie, R.string.q_description_lie));
        map(new QuestionCode(R.string.q_irresponsibility, R.string.q_text_irresponsibility, R.string.q_description_irresponsibility));
        map(new QuestionCode(R.string.q_do_body, R.string.q_text_do_body, R.string.q_description_do_body));
        map(new QuestionCode(R.string.q_do_close, R.string.q_text_do_close, R.string.q_description_do_close));
        map(new QuestionCode(R.string.q_do_others, R.string.q_text_do_others, R.string.q_description_do_others));
    }

    private static void map(QuestionCode qc) {
        QUESTION_CODE_MAP.put(qc.code, qc);
    }

    public static class QuestionCode {
        private final int code;
        private final int textId;
        private final int descriptionId;

        private QuestionCode(int code, int textId, int descriptionId) {
            this.code = code;
            this.textId = textId;
            this.descriptionId = descriptionId;
        }

        public int getTextId() {
            return textId;
        }

        public int getDescriptionId() {
            return descriptionId;
        }

    }

    private QuestionContract() {
    }
}
