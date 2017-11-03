package feelings.guide.question;

public class Question {
    /**
     * Question ID.
     */
    private long id;
    /**
     * Question text.
     */
    private String text;
    /**
     * Is user-defined question
     */
    private final boolean isUser;
    /**
     * Is deleted
     */
    private boolean isDeleted = false;

    public Question(String text) {
        this.text = text;
        isUser = true;
    }

    public Question(long id, String text) {
        this.id = id;
        this.text = text;
        isUser = true;
    }

    Question(long id, String text, boolean isUser, boolean isDeleted) {
        this.id = id;
        this.text = text;
        this.isUser = isUser;
        this.isDeleted = isDeleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    boolean isUser() {
        return isUser;
    }

    boolean isDeleted() {
        return isDeleted;
    }
}
