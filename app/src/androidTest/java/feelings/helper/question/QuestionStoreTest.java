package feelings.helper.question;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static feelings.helper.question.QuestionContract.COLUMN_IS_DELETED;
import static feelings.helper.question.QuestionContract.COLUMN_IS_USER;
import static feelings.helper.question.QuestionContract.COLUMN_TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QuestionStoreTest {

    private static final String TO_BE_OR_NOT_TO_BE = "To be or not to be?";
    private static final String WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING = "Would you like to understand nothing?";

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private long questionId;
    private long questionId2;

    @BeforeClass
    public static void beforeClass() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @AfterClass
    public static void afterClass() {
        context = null;
    }

    @Before
    public void before() {
        questionId = 0;
        questionId2 = 0;
    }

    @After
    public void after() {
        if (questionId != 0) {
            QuestionStore.deleteQuestion(context, questionId);
        }
        if (questionId2 != 0) {
            QuestionStore.deleteQuestion(context, questionId2);
        }
    }

    @Test
    public void testGetById() {
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
    public void testCreation() {
        // when
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));
        questionId2 = QuestionStore.createQuestion(context, new Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING));

        // then
        assertTrue(questionId != -1);
        assertTrue(questionId2 != -1);
        assertNotEquals(questionId, questionId2);
    }

    @Test
    public void testUpdate() {
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
    public void testDelete() {
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
    public void testGetAll() {
        // given
        questionId = QuestionStore.createQuestion(context, new Question(TO_BE_OR_NOT_TO_BE));
        questionId2 = QuestionStore.createQuestion(context, new Question(WOULD_YOU_LIKE_TO_UNDERSTAND_NOTHING));
        QuestionStore.deleteQuestion(context, questionId2);

        // when
        Cursor cursor = QuestionStore.getAll(context);

        // then
        List<Question> questions = cursorToQuestions(cursor);
        assertTrue(questions.size() > 0);

        boolean containsQuestion1 = false;
        boolean containsQuestion2 = false;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                containsQuestion1 = true;
            } else if (question.getId() == questionId2) {
                containsQuestion2 = true;
            }
        }
        assertTrue(containsQuestion1);
        assertFalse(containsQuestion2);
    }

    private List<Question> cursorToQuestions(Cursor cursor) {
        assertNotNull(cursor);
        try {
            List<Question> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT));
                boolean isUser = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER));
                boolean isDeleted = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DELETED));
                list.add(new Question(id, text, isUser, isDeleted));
            }
            return list;
        } finally {
            cursor.close();
        }
    }
}
