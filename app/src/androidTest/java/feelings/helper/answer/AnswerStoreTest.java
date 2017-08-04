package feelings.helper.answer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import java.util.List;

import static junit.framework.Assert.assertEquals;
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
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME, ANSWER_TEXT));
        AnswerStore.saveAnswer(context, new Answer(2, DATE_TIME, "another one"));
        AnswerStore.saveAnswer(context, new Answer(3, DATE_TIME, "one more"));

        List<Answer> answers = AnswerStore.getAllAnswers(context);

        assertEquals(3, answers.size());
        Answer fromDb = answers.get(0);
        assertEquals(QUESTION_ID, fromDb.getQuestionId());
        assertEquals(DATE_TIME, fromDb.getDateTime());
        assertEquals(ANSWER_TEXT, fromDb.getAnswerText());
    }
}
