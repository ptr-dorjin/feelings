package feelings.guide.answer;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import feelings.guide.util.DateTimeUtil;

import static feelings.guide.answer.AnswerContract.COLUMN_ANSWER;
import static feelings.guide.answer.AnswerContract.COLUMN_DATE_TIME;
import static feelings.guide.answer.AnswerContract.COLUMN_QUESTION_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AnswerStoreTest {
    private static final long QUESTION_ID = 1;
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();
    private static final String ANSWER_TEXT = "test answer";

    private Context context;

    @BeforeClass
    public static void beforeClass() {
        AnswerStore.deleteAll(InstrumentationRegistry.getTargetContext());
    }

    @Before
    public void before() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void after() {
        AnswerStore.deleteAll(context);
        context = null;
    }

    @Test
    public void testCreate() {
        Answer answer = new Answer(QUESTION_ID, DATE_TIME, ANSWER_TEXT);

        boolean created = AnswerStore.saveAnswer(context, answer);
        assertTrue(created);
    }

    @Test
    public void testGetAll() {
        // given
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT));
        AnswerStore.saveAnswer(context, new Answer(2, DATE_TIME.minusMinutes(1), "another one"));
        AnswerStore.saveAnswer(context, new Answer(3, DATE_TIME.minusMinutes(2), "one more"));

        // when
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));

        // then
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
        // given
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT));
        AnswerStore.saveAnswer(context, new Answer(2, DATE_TIME.minusMinutes(1), "another one"));
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(2), "one more"));

        // when
        List<Answer> answers = cursorToAnswers(AnswerStore.getByQuestionId(context, QUESTION_ID));

        // then
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
                long questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID));
                LocalDateTime dateTime = LocalDateTime.parse(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                        DateTimeUtil.DB_FORMATTER);
                String answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));
                list.add(new Answer(questionId, dateTime, answerText));
            }
            return list;
        } finally {
            cursor.close();
        }
    }
}
