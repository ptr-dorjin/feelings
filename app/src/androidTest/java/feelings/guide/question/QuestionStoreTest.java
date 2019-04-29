package feelings.guide.question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import feelings.guide.db.DbHelper;

import static com.google.common.truth.Truth.assertThat;
import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.guide.question.QuestionContract.COLUMN_IS_HIDDEN;
import static feelings.guide.question.QuestionContract.COLUMN_IS_USER;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;

@RunWith(AndroidJUnit4.class)
public class QuestionStoreTest {

    private static final String TO_BE_OR_NOT_TO_BE = "To be or not to be?";
    private static final String WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING = "Would you like to understand nothing?";
    private static final String SOME_BUILT_IN_QUESTION = "Some built-in question";
    private static final String TEST_BUILT_IN_QUESTION_CODE = "test_code";

    private static Context context;
    private long questionId;
    private long questionId2;
    private long questionId3;

    @BeforeClass
    public static void beforeClass() {
        context = ApplicationProvider.getApplicationContext();
    }

    @AfterClass
    public static void afterClass() {
        context = null;
    }

    @Before
    public void before() {
        questionId = 0;
        questionId2 = 0;
        questionId3 = 0;
    }

    @After
    public void after() {
        if (questionId != 0) {
            QuestionStore.deleteQuestion(context, questionId);
        }
        if (questionId2 != 0) {
            QuestionStore.deleteQuestion(context, questionId2);
        }
        if (questionId3 != 0) {
            QuestionStore.deleteQuestion(context, questionId3);
        }

        deleteBuiltInQuestion(TEST_BUILT_IN_QUESTION_CODE);
    }

    @Test
    public void shouldGetById() {
        // given
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));

        // when
        Question fromDB = QuestionStore.getById(context, questionId);
        Question fromDB2 = QuestionStore.getById(context, questionId + 1);

        // then
        assertThat(fromDB).isNotNull();
        assertThat(fromDB2).isNull();
    }

    @Test
    public void shouldCreate() {
        // when
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));
        questionId2 = QuestionStore.createQuestion(context, new Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING));

        // then
        assertThat(questionId).isNotEqualTo(-1);
        assertThat(questionId2).isNotEqualTo(-1);
        assertThat(questionId).isNotEqualTo(questionId2);
    }

    @Test
    public void shouldUpdate() {
        // given
        Question question = new Question(TO_BE_OR_NOT_TO_BE);
        questionId = QuestionStore.createQuestion(context, question);
        question.setId(questionId);
        question.setText(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING);

        // when
        boolean updated = QuestionStore.updateQuestion(context, question);

        // then
        assertThat(updated).isTrue();
        Question fromDB = QuestionStore.getById(context, questionId);
        assertThat(fromDB).isNotNull();
        assertThat(fromDB.getText()).isEqualTo(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING);
    }

    @Test
    public void shouldDelete() {
        // given
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));

        // when
        boolean deleted = QuestionStore.deleteQuestion(context, questionId);

        // then
        assertThat(deleted).isTrue();
        Question fromDB = QuestionStore.getById(context, questionId);
        assertThat(fromDB).isNotNull();
        assertThat(fromDB.isDeleted()).isTrue();
    }

    @Test
    public void shouldHideBuiltInQuestion() {
        // given
        Question builtInQuestion = new Question();
        builtInQuestion.setCode(TEST_BUILT_IN_QUESTION_CODE);
        builtInQuestion.setText(SOME_BUILT_IN_QUESTION);
        questionId = createBuiltInQuestion(builtInQuestion);

        // when
        boolean hidden = QuestionStore.hideQuestion(context, questionId);

        // then
        assertThat(hidden).isTrue();
        Question fromDB = QuestionStore.getById(context, questionId);
        assertThat(fromDB).isNotNull();
        assertThat(fromDB.isHidden()).isTrue();
    }

    @Test
    public void shouldGetAll() {
        // given
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));

        questionId2 = QuestionStore.createQuestion(context, new Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING));
        QuestionStore.deleteQuestion(context, questionId2);

        Question builtInQuestion = new Question();
        builtInQuestion.setCode(TEST_BUILT_IN_QUESTION_CODE);
        builtInQuestion.setText(SOME_BUILT_IN_QUESTION);
        questionId3 = createBuiltInQuestion(builtInQuestion);
        QuestionStore.hideQuestion(context, questionId3);

        // when
        Cursor cursor = QuestionStore.getAll(context);

        // then
        List<Question> questions = cursorToQuestions(cursor);
        assertThat(questions.size()).isGreaterThan(0); //also contains real app's questions

        boolean containsQuestion1 = false;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                containsQuestion1 = true;
            }
            assertThat(question.getId()).isNotEqualTo(questionId2);
            assertThat(question.getId()).isNotEqualTo(questionId3);
        }
        assertThat(containsQuestion1).isTrue();
    }

    private List<Question> cursorToQuestions(Cursor cursor) {
        assertThat(cursor).isNotNull();
        try {
            List<Question> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(QuestionStore.cursorToQuestion(cursor));
            }
            return list;
        } finally {
            cursor.close();
        }
    }

    private static long createBuiltInQuestion(Question question) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, question.getText());
            values.put(COLUMN_CODE, question.getCode());
            values.put(COLUMN_DESCRIPTION, question.getDescription());
            values.put(COLUMN_IS_USER, false);
            values.put(COLUMN_IS_DELETED, false);
            values.put(COLUMN_IS_HIDDEN, false);

            return db.insert(QUESTION_TABLE, null, values);
        }
    }

    private static void deleteBuiltInQuestion(String questionCode) {
        try (SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase()) {
            String selection = COLUMN_CODE + " = ?";
            String[] selectionArgs = {questionCode};
            db.delete(QUESTION_TABLE, selection, selectionArgs);
        }

    }
}
