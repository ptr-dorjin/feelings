package feelings.helper.ui.schedule.fragments;

import android.support.v4.app.Fragment;

import feelings.helper.schedule.Schedule;
import feelings.helper.ui.schedule.ScheduleActivity;
import feelings.helper.util.ToastUtil;

public abstract class AbstractFragment extends Fragment {
    Schedule schedule; // Schedule from DB or initial

    public abstract String getFragmentTag();

    boolean initSchedule() {
        schedule = getArguments().getParcelable(ScheduleActivity.SCHEDULE_PARCEL_KEY);
        if (schedule == null) {
            ToastUtil.showLong(getContext(), "Schedule is null!");
            return false;
        }
        return true;
    }

    public void onSwitchOnOffChanged(boolean isOn) {
        // to be overridden
    }
}
