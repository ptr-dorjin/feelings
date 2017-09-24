package feelings.helper.answer;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import feelings.helper.util.DateTimeUtil;

import static feelings.helper.answer.AnswerContract.COLUMN_NAME_ANSWER;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_DATE_TIME;
import static feelings.helper.answer.AnswerContract.COLUMN_NAME_QUESTION_ID;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AnswerStoreTest {
    private static final int QUESTION_ID = 1;
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();
    private static final String ANSWER_TEXT = "test answer";

    @BeforeClass
    public static void beforeClass() {
        AnswerStore.deleteAll(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
        Context context = InstrumentationRegistry.getTargetContext();
        AnswerStore.deleteAll(context);
    }

    @Test
    public void testCreate() {
        Context context = InstrumentationRegistry.getTargetContext();
        Answer answer = new Answer(QUESTION_ID, DATE_TIME, ANSWER_TEXT);

        boolean created = AnswerStore.saveAnswer(context, answer);
        assertTrue(created);
    }

    @Test
    public void testGetAll() {
        Context context = InstrumentationRegistry.getTargetContext();
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT));
        AnswerStore.saveAnswer(context, new Answer(2, DATE_TIME.minusMinutes(1), "another one"));
        AnswerStore.saveAnswer(context, new Answer(3, DATE_TIME.minusMinutes(2), "one more"));

        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));

        assertEquals(3, answers.size());
        Answer fromDb = answers.get(0);
        assertEquals(2, fromDb.getQuestionId());
        assertEquals(DATE_TIME.minusMinutes(1), fromDb.getDateTime());
        assertEquals("another one", fromDb.getAnswerText());

        fromDb = answers.get(1);
        assertEquals(3, fromDb.getQuestionId());
        assertEquals(DATE_TIME.minusMinutes(2), fromDb.getDateTime());
        assertEquals("one more", fromDb.getAnswerText());

        fromDb = answers.get(2);
        assertEquals(QUESTION_ID, fromDb.getQuestionId());
        assertEquals(DATE_TIME.minusMinutes(3), fromDb.getDateTime());
        assertEquals(ANSWER_TEXT, fromDb.getAnswerText());
    }

    @Test
    public void testGetByQuestionId() {
        Context context = InstrumentationRegistry.getTargetContext();
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT));
        AnswerStore.saveAnswer(context, new Answer(2, DATE_TIME.minusMinutes(1), "another one"));
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(2), "one more"));

        List<Answer> answers = cursorToAnswers(AnswerStore.getByQuestionId(context, QUESTION_ID));

        assertEquals(2, answers.size());
        Answer fromDb = answers.get(0);
        assertEquals(QUESTION_ID, fromDb.getQuestionId());
        assertEquals(DATE_TIME.minusMinutes(2), fromDb.getDateTime());
        assertEquals("one more", fromDb.getAnswerText());

        fromDb = answers.get(1);
        assertEquals(QUESTION_ID, fromDb.getQuestionId());
        assertEquals(DATE_TIME.minusMinutes(3), fromDb.getDateTime());
        assertEquals(ANSWER_TEXT, fromDb.getAnswerText());
    }

    private List<Answer> cursorToAnswers(Cursor cursor) {
        assertNotNull(cursor);
        try {
            List<Answer> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID));
                LocalDateTime dateTime = LocalDateTime.parse(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE_TIME)),
                        DateTimeUtil.DB_FORMATTER);
                String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ANSWER));
                list.add(new Answer(questionId, dateTime, answerText));
            }
            return list;
        } finally {
            cursor.close();
        }
    }
}
