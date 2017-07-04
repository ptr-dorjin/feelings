package feelings.helper.questions;

public class Question {
    /**
     * Question ID.
     * [0, 100] range is for built-in questions
     * [100, ...) range is for user-defined questions
     */
    private final int id;
    /**
     * Question text.
     */
    private String text;
    /**
     * Is user defined question
     */
    private final boolean isUser;

    Question(int id, String text) {
        this.id = id;
        this.text = text;
        this.isUser = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;

        Question question = (Question) o;

        if (id != question.id) return false;
        if (isUser != question.isUser) return false;
        return text.equals(question.text);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + text.hashCode();
        result = 31 * result + (isUser ? 1 : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUser() {
        return isUser;
    }
}
