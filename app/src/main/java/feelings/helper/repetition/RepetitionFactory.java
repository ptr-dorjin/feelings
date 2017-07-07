package feelings.helper.repetition;

public class RepetitionFactory {

    public static Repetition create(String type, String asString) {
        switch (type) {
            case HourlyRepetition.HOURLY:
                return new HourlyRepetition(asString);
            case DailyRepetition.DAILY:
                return new DailyRepetition(asString);
            case WeeklyRepetition.WEEKLY:
                return new WeeklyRepetition(asString);
            case WeeklyCustomRepetition.WEEKLY_CUSTOM:
                return new WeeklyCustomRepetition(asString);
            default:
                throw new RuntimeException("Unexpected repetition type: " + type);
        }
    }
}
