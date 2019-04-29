package feelings.guide.answer;

import android.content.Context;
import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import feelings.guide.question.Question;
import feelings.guide.question.QuestionService;
import feelings.guide.util.DateTimeUtil;

import static com.google.common.truth.Truth.assertThat;
import static feelings.guide.answer.AnswerContract.COLUMN_ANSWER;
import static feelings.guide.answer.AnswerContract.COLUMN_DATE_TIME;
import static feelings.guide.answer.AnswerContract.COLUMN_QUESTION_ID;

@RunWith(AndroidJUnit4.class)
public class AnswerStoreTest {
    private static final long ID = 1;
    private static final long QUESTION_ID = 1;
    private static final int QUESTION_ID_2 = 2;
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();
    private static final String ANSWER_TEXT = "test answer";

    private Context context;

    @BeforeClass
    public static void beforeClass() {
        AnswerStore.deleteAll(ApplicationProvider.getApplicationContext());
    }

    @Before
    public void before() {
        context = ApplicationProvider.getApplicationContext();
    }

    @After
    public void after() {
        AnswerStore.deleteAll(context);
        context = null;
    }

    @Ignore("for performance tests only")
    @Test
    public void createForPerformanceTest() {
        IntStream.range(0, 10000).forEach(i -> {
            boolean created = AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusSeconds(i), ANSWER_TEXT + i));
            assertThat(created).isTrue();
        });

    }

    @Test
    public void testCreate() {
        // given
        Answer answer = new Answer(QUESTION_ID, DATE_TIME, ANSWER_TEXT);

        // when
        boolean created = AnswerStore.saveAnswer(context, answer);

        // then
        assertThat(created).isTrue();
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));
        assertThat(answers)
                .containsExactly(answer);
    }

    @Test
    public void testCreateWithId() {
        Answer answer = new Answer(ID, QUESTION_ID, DATE_TIME, ANSWER_TEXT);

        // when
        boolean created = AnswerStore.saveAnswer(context, answer);

        // then
        assertThat(created).isTrue();
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));
        assertThat(answers)
                .containsExactly(answer);
    }

    @Test
    public void testGetAll() {
        // given
        Answer answer1 = new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT);
        Answer answer2 = new Answer(QUESTION_ID_2, DATE_TIME.minusMinutes(1), "another one");
        Answer answer3 = new Answer(3, DATE_TIME.minusMinutes(2), "one more");
        AnswerStore.saveAnswer(context, answer1);
        AnswerStore.saveAnswer(context, answer2);
        AnswerStore.saveAnswer(context, answer3);

        // when
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));

        // then
        assertThat(answers)
                .containsExactly(answer2, answer3, answer1)
                .inOrder();
    }

    @Test
    public void testGetByQuestionId() {
        // given
        Answer answer1 = new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT);
        Answer answer2 = new Answer(QUESTION_ID_2, DATE_TIME.minusMinutes(1), "another one");
        Answer answer3 = new Answer(QUESTION_ID, DATE_TIME.minusMinutes(2), "one more");
        AnswerStore.saveAnswer(context, answer1);
        AnswerStore.saveAnswer(context, answer2);
        AnswerStore.saveAnswer(context, answer3);

        // when
        List<Answer> answers = cursorToAnswers(AnswerStore.getByQuestionId(context, QUESTION_ID));

        // then
        assertThat(answers)
                .containsExactly(answer3, answer1)
                .inOrder();
    }

    @Test
    public void testDeleteById() {
        // given
        Answer answer1 = new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT);
        Answer answer2 = new Answer(QUESTION_ID_2, DATE_TIME.minusMinutes(1), "another one");
        AnswerStore.saveAnswer(context, answer1);
        AnswerStore.saveAnswer(context, answer2);

        // when
        AnswerStore.deleteById(context, answer1.getId());

        // then
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));
        assertThat(answers)
                .containsExactly(answer2);
    }

    @Test
    public void testDeleteByQuestionId() {
        // given
        Answer answer = new Answer(QUESTION_ID_2, DATE_TIME.minusMinutes(1), "another one");
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(3), ANSWER_TEXT));
        AnswerStore.saveAnswer(context, answer);
        AnswerStore.saveAnswer(context, new Answer(QUESTION_ID, DATE_TIME.minusMinutes(2), "one more"));

        // when
        AnswerStore.deleteByQuestionId(context, QUESTION_ID);

        // then
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));
        assertThat(answers)
                .containsExactly(answer);
    }

    @Test
    public void testDeleteForDeletedQuestions() {
        // given
        long questionId = QuestionService.createQuestion(context, new Question("test question"));
        Answer answer = new Answer(questionId, DATE_TIME.minusMinutes(3), ANSWER_TEXT);
        Answer answer2 = new Answer(QUESTION_ID, DATE_TIME.minusMinutes(2), "one more");
        AnswerStore.saveAnswer(context, answer);
        AnswerStore.saveAnswer(context, answer2);

        // when
        QuestionService.deleteQuestion(context, questionId);
        AnswerStore.deleteForDeletedQuestions(context);

        // then
        List<Answer> answers = cursorToAnswers(AnswerStore.getAll(context));
        assertThat(answers)
                .containsExactly(answer2);
    }

    private List<Answer> cursorToAnswers(Cursor cursor) {
        assertThat(cursor).isNotNull();
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
