package feelings.guide.question;

public class Question {
    private long id;
    private String code;
    private String text;
    private String description;
    private final boolean isUser;
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

    Question(long id, String code, String text, String description, boolean isUser, boolean isDeleted) {
        this.id = id;
        this.code = code;
        this.text = text;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    boolean isUser() {
        return isUser;
    }

    boolean isDeleted() {
        return isDeleted;
    }
}
