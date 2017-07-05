package feelings.helper.repetition;

public class RepetitionSetting {
    private int questionId;
    private boolean isOn;
    private Repetition repetition;

    RepetitionSetting(int questionId, boolean isOn, Repetition repetition) {
        this.questionId = questionId;
        this.isOn = isOn;
        this.repetition = repetition;
    }

    public int getQuestionId() {
        return questionId;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }
}
