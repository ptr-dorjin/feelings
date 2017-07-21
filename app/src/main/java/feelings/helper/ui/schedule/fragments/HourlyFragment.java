package feelings.helper.ui.schedule.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import feelings.helper.R;

import static feelings.helper.ui.schedule.fragments.FragmentFactory.HOURLY_TAG;

public class HourlyFragment extends AbstractFragment {

    @Override
    String getFragmentTag() {
        return HOURLY_TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hourly_repeat, container, false);
    }
}
