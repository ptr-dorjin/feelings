package feelings.helper.ui.schedule.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import feelings.helper.R;

import static feelings.helper.ui.schedule.fragments.FragmentFactory.WEEKLY_CUSTOM_TAG;

public class WeeklyCustomFragment extends AbstractFragment {

    @Override
    String getFragmentTag() {
        return WEEKLY_CUSTOM_TAG;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_custom_repeat, container, false);
    }
}
