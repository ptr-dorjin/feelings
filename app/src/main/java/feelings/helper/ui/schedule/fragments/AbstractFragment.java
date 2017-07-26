package feelings.helper.ui.schedule.fragments;

import android.support.v4.app.Fragment;

import feelings.helper.schedule.Schedule;

public abstract class AbstractFragment extends Fragment {
    Schedule schedule; // Schedule from DB or initial

    public abstract String getFragmentTag();
}
