package feelings.guide.question;

import android.annotation.SuppressLint;
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

import static feelings.guide.question.QuestionContract.COLUMN_CODE;
import static feelings.guide.question.QuestionContract.COLUMN_DESCRIPTION;
import static feelings.guide.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.guide.question.QuestionContract.COLUMN_IS_HIDDEN;
import static feelings.guide.question.QuestionContract.COLUMN_IS_USER;
import static feelings.guide.question.QuestionContract.COLUMN_TEXT;
import static feelings.guide.question.QuestionContract.QUESTION_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QuestionStoreTest {

    private static final String TO_BE_OR_NOT_TO_BE = "To be or not to be?";
    private static final String WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING = "Would you like to understand nothing?";
    private static final String SOME_BUILT_IN_QUESTION = "Some built-in question";
    private static final String TEST_BUILT_IN_QUESTION_CODE = "test_code";

    @SuppressLint("StaticFieldLeak")
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
        assertNotNull(fromDB);
        assertNull(fromDB2);
    }

    @Test
    public void shouldCreate() {
        // when
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));
        questionId2 = QuestionStore.createQuestion(context, new Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING));

        // then
        assertTrue(questionId != -1);
        assertTrue(questionId2 != -1);
        assertNotEquals(questionId, questionId2);
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
        assertTrue(updated);
        Question fromDB = QuestionStore.getById(context, questionId);
        assertNotNull(fromDB);
        assertEquals(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING, fromDB.getText());
    }

    @Test
    public void shouldDelete() {
        // given
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));

        // when
        boolean deleted = QuestionStore.deleteQuestion(context, questionId);

        // then
        assertTrue(deleted);
        Question fromDB = QuestionStore.getById(context, questionId);
        assertNotNull(fromDB);
        assertTrue(fromDB.isDeleted());
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
        assertTrue(hidden);
        Question fromDB = QuestionStore.getById(context, questionId);
        assertNotNull(fromDB);
        assertTrue(fromDB.isHidden());
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
        assertTrue(questions.size() > 0); //also contains real app's questions

        boolean containsQuestion1 = false;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                containsQuestion1 = true;
            }
            assertNotEquals(questionId2, question.getId());
            assertNotEquals(questionId3, question.getId());
        }
        assertTrue(containsQuestion1);
    }

    private List<Question> cursorToQuestions(Cursor cursor) {
        assertNotNull(cursor);
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
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = {questionCode};

        try {
            db.delete(QUESTION_TABLE,selection, selectionArgs);
        } finally {
            db.close();
        }

    }
}
