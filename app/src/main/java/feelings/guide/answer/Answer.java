package feelings.guide.answer;

import androidx.annotation.NonNull;

import org.threeten.bp.LocalDateTime;

public class Answer {
    private long id;
    private final long questionId;
    private final LocalDateTime dateTime;
    private String answerText;

    public Answer(long questionId, LocalDateTime dateTime, String answerText) {
        this.questionId = questionId;
        this.dateTime = dateTime;
        this.answerText = answerText;
    }

    /**
     * Ctor for cases when ID is known beforehand. Ex: restoring deleted answer
     */
    Answer(long id, long questionId, LocalDateTime dateTime, String answerText) {
        this(questionId, dateTime, answerText);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    // equals and hashCode for tests
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (questionId != answer.questionId) return false;
        if (!dateTime.equals(answer.dateTime)) return false;
        return answerText.equals(answer.answerText);
    }

    @Override
    public int hashCode() {
        int result = (int) (questionId ^ (questionId >>> 32));
        result = 31 * result + dateTime.hashCode();
        result = 31 * result + answerText.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", dateTime=" + dateTime +
                ", answerText='" + answerText + '\'' +
                '}';
    }
}
