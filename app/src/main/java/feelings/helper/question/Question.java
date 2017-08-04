package feelings.helper.question;

import android.support.annotation.NonNull;

public class Question implements Comparable<Question> {
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
     * Is user-defined question
     */
    private final boolean isUser;

    Question(int id, String text) {
        this.id = id;
        this.text = text;
        this.isUser = false;
    }

    @Override
    public int compareTo(@NonNull Question o) {
        return Integer.valueOf(id).compareTo(o.id);
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
