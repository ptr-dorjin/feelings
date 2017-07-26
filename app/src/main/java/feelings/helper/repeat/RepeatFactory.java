package feelings.helper.repeat;

public class RepeatFactory {

    public static Repeat create(RepeatType type, String asString) {
        switch (type) {
            case HOURLY:
                return new HourlyRepeat(asString);
            case DAILY:
                return new DailyRepeat(asString);
            case WEEKLY:
                return new WeeklyRepeat(asString);
            case WEEKLY_CUSTOM:
                return new WeeklyCustomRepeat(asString);
            default:
                throw new IllegalArgumentException("Unexpected repeat type: " + type);
        }
    }
}
