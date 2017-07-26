package feelings.helper.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import feelings.helper.repeat.Repeat;
import feelings.helper.repeat.RepeatFactory;
import feelings.helper.repeat.RepeatType;

public class Schedule implements Parcelable {
    private int questionId;
    private boolean isOn;
    private RepeatType repeatType;
    private Repeat repeat;

    public Schedule(int questionId, boolean isOn, RepeatType repeatType, Repeat repeat) {
        this.questionId = questionId;
        this.isOn = isOn;
        this.repeatType = repeatType;
        this.repeat = repeat;
    }

    /**
     * Order must be the same as in writeToParcel()
     */
    private Schedule(Parcel parcel) {
        questionId = parcel.readInt();
        isOn = parcel.readInt() == 1;
        repeatType = RepeatType.valueOf(parcel.readString());
        repeat = RepeatFactory.create(repeatType, parcel.readString());
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "questionId=" + questionId +
                ", isOn=" + isOn +
                ", repeatType=" + repeatType +
                ", repeat=" + repeat.toDbString() +
                '}';
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

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    /* Parcelable members */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(questionId);
        parcel.writeInt(isOn ? 1 : 0);
        parcel.writeString(repeatType.name());
        parcel.writeString(repeat.toDbString());
    }

    public static final Parcelable.Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel parcel) {
            return new Schedule(parcel);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}
