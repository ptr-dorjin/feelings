package feelings.guide.question;

public class Question {
    private long id;
    private String code; //only for system questions
    private String text;
    private String description; //only for system questions
    private boolean isUser;
    private boolean isDeleted; //can be set for user questions - by user, for system questions - only during app update
    private boolean isHidden; //only for system questions. can be done by user

    public Question(String text) {
        this.text = text;
        isUser = true;
    }

    public Question(long id, String text) {
        this.id = id;
        this.text = text;
        isUser = true;
    }

    Question() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public void setDescription(String description) {
        this.description = description;
    }

    boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
