package feelings.helper.ui.schedule.fragments;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.threeten.bp.LocalTime;

import java.util.TreeSet;

import feelings.helper.R;
import feelings.helper.repeat.DailyRepeat;
import feelings.helper.repeat.Repeat;
import feelings.helper.repeat.RepeatType;
import feelings.helper.ui.UiUtil;

public class DailyFragment extends AbstractFragment {

    private DailyRepeat repeat;
    private View fragmentRoot;
    private DailyTimesAdapter adapter;
    private FloatingActionButton fab;

    @Override
    public String getFragmentTag() {
        return RepeatType.DAILY.name();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!initSchedule()) {
            return null;
        }
        initRepeat();

        fragmentRoot = inflater.inflate(R.layout.fragment_daily_repeat, container, false);
        fab = (FloatingActionButton) fragmentRoot.findViewById(R.id.daily_add);
        setUpTimesList();
        setUpFab();
        updateFabColor(schedule.isOn());

        UiUtil.disableEnableControls(schedule.isOn(), fragmentRoot);
        return fragmentRoot;
    }

    private void initRepeat() {
        Repeat repeat = schedule.getRepeat();
        if (!(repeat instanceof DailyRepeat)) {
            repeat = new DailyRepeat(new TreeSet<LocalTime>());
            schedule.setRepeat(repeat);
        }
        this.repeat = (DailyRepeat) repeat;
    }

    private void setUpTimesList() {
        RecyclerView rv = (RecyclerView) fragmentRoot.findViewById(R.id.daily_times_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new DailyTimesAdapter(repeat.getTimes(), schedule.isOn());
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    private void setUpFab() {
        FloatingActionButton addButton = (FloatingActionButton) fragmentRoot.findViewById(R.id.daily_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalTime now = LocalTime.now();
                new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                repeat.getTimes().add(LocalTime.of(hour, minute));
                                // refresh all due to position might be changed
                                adapter.notifyDataSetChanged();
                            }
                        },
                        now.getHour(), now.getMinute(), true)
                        .show();

            }
        });
    }

    @Override
    public void onSwitchOnOffChanged(boolean isOn) {
        updateFabColor(isOn);
    }

    private void updateFabColor(boolean isOn) {
        int color = isOn ? R.color.colorAccent : R.color.colorDisabled;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = getResources().getColor(color, null);
        } else {
            color = getResources().getColor(color);
        }
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
