package feelings.helper.ui.schedule.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import feelings.helper.R;
import feelings.helper.repeat.RepeatType;

public class WeeklyCustomFragment extends AbstractFragment {

    @Override
    public String getFragmentTag() {
        return RepeatType.WEEKLY_CUSTOM.name();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_custom_repeat, container, false);
    }
}
