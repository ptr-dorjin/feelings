package feelings.helper.ui.schedule.fragments;

import android.os.Bundle;

import feelings.helper.repeat.RepeatType;
import feelings.helper.schedule.Schedule;
import feelings.helper.ui.schedule.ScheduleActivity;

public class FragmentFactory {
    public static String getTag(int position) {
        return RepeatType.values()[position].name();
    }

    public static AbstractFragment create(int position, Schedule schedule) {
        // order must be as in R.arrays.repeat_type_array
        AbstractFragment fragment;
        switch (position) {
            case 0:
                fragment = new HourlyFragment();
                break;
            case 1:
                fragment = new DailyFragment();
                break;
            case 2:
                fragment = new WeeklyFragment();
                break;
            case 3:
                fragment = new WeeklyCustomFragment();
                break;
            default:
                throw new IllegalArgumentException("Unexpected position: " + position);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(ScheduleActivity.SCHEDULE_PARCEL_KEY, schedule);
        fragment.setArguments(bundle);
        return fragment;
    }
}
