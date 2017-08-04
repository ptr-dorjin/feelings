package feelings.helper.answer;

import org.threeten.bp.LocalDateTime;

public class Answer {
    private final int questionId;
    private final LocalDateTime dateTime;
    private final String answerText;

    public Answer(int questionId, LocalDateTime dateTime, String answerText) {
        this.questionId = questionId;
        this.dateTime = dateTime;
        this.answerText = answerText;
    }

    public int getQuestionId() {
        return questionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getAnswerText() {
        return answerText;
    }
}
