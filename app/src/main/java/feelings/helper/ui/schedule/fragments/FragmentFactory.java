package feelings.helper.ui.schedule.fragments;

import android.support.v4.app.Fragment;

public class FragmentFactory {
    static final String HOURLY_TAG = "hourly_fragment";
    static final String DAILY_TAG = "daily_fragment";
    static final String WEEKLY_TAG = "weekly_fragment";
    static final String WEEKLY_CUSTOM_TAG = "weekly_custom_fragment";

    private static final String[] arr = new String[] {
        HOURLY_TAG, DAILY_TAG, WEEKLY_TAG, WEEKLY_CUSTOM_TAG
    };

    public static String getTag(int position) {
        return arr[position];
    }

    public static Fragment create(int position) {
        // order must be as in R.arrays.repeat_type_array
        switch (position) {
            case 0: return new HourlyFragment();
            case 1: return new DailyFragment();
            case 2: return new WeeklyFragment();
            case 3: return new WeeklyCustomFragment();
        }
        throw new RuntimeException("Unexpected position=" + position);
    }
}
