package feelings.helper.settings;

import feelings.helper.repeat.Repeat;

public class Settings {
    private int questionId;
    private boolean isOn;
    private Repeat repeat;

    public Settings(int questionId, boolean isOn, Repeat repeat) {
        this.questionId = questionId;
        this.isOn = isOn;
        this.repeat = repeat;
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

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }
}
