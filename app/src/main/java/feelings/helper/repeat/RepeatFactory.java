package feelings.helper.repeat;

public class RepeatFactory {

    public static Repeat create(String type, String asString) {
        switch (type) {
            case HourlyRepeat.HOURLY:
                return new HourlyRepeat(asString);
            case DailyRepeat.DAILY:
                return new DailyRepeat(asString);
            case WeeklyRepeat.WEEKLY:
                return new WeeklyRepeat(asString);
            case WeeklyCustomRepeat.WEEKLY_CUSTOM:
                return new WeeklyCustomRepeat(asString);
            default:
                throw new RuntimeException("Unexpected repeat type: " + type);
        }
    }
}
