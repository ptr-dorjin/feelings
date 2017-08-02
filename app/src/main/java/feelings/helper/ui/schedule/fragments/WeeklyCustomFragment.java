package feelings.helper.ui.schedule.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import feelings.helper.R;
import feelings.helper.repeat.Repeat;
import feelings.helper.repeat.RepeatType;
import feelings.helper.repeat.WeeklyCustomRepeat;
import feelings.helper.ui.UiUtil;
import feelings.helper.util.DateTimeUtil;

import static feelings.helper.repeat.Repeat.HOUR_FORMATTER;
import static feelings.helper.repeat.Repeat.MINUTE_FORMATTER;

public class WeeklyCustomFragment extends AbstractFragment {

    private static final int[] DAYS_OF_WEEK_VIEW_IDS = new int[]{
            R.id.weekly_custom_monday,
            R.id.weekly_custom_tuesday,
            R.id.weekly_custom_wednesday,
            R.id.weekly_custom_thursday,
            R.id.weekly_custom_friday,
            R.id.weekly_custom_saturday,
            R.id.weekly_custom_sunday
    };

    private WeeklyCustomRepeat repeat;
    private View fragmentRoot;
    private List<ToggleButton> daysOfWeek = new ArrayList<>(DAYS_OF_WEEK_VIEW_IDS.length);
    private TextView hour;

    private TextView minute;

    @Override
    public String getFragmentTag() {
        return RepeatType.WEEKLY_CUSTOM.name();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!initSchedule()) {
            return null;
        }
        initRepeat();

        fragmentRoot = inflater.inflate(R.layout.fragment_weekly_custom_repeat, container, false);
        for (int dayOfWeekViewId : DAYS_OF_WEEK_VIEW_IDS) {
            daysOfWeek.add((ToggleButton) fragmentRoot.findViewById(dayOfWeekViewId));
        }
        hour = (TextView) fragmentRoot.findViewById(R.id.weekly_custom_time_hour);
        minute = (TextView) fragmentRoot.findViewById(R.id.weekly_custom_time_minute);

        setUpDaysOfWeek();
        setUpTime();

        UiUtil.disableEnableControls(schedule.isOn(), fragmentRoot);
        return fragmentRoot;
    }

    private void initRepeat() {
        Repeat repeat = schedule.getRepeat();
        if (!(repeat instanceof WeeklyCustomRepeat)) {
            repeat = new WeeklyCustomRepeat(new TreeSet<DayOfWeek>(), LocalTime.of(8, 0));
            schedule.setRepeat(repeat);
        }
        this.repeat = (WeeklyCustomRepeat) repeat;
    }

    private void setUpDaysOfWeek() {
        //set localized days of week names
        for (int i = 0; i < daysOfWeek.size(); i++) {
            ToggleButton dayOfWeekBtn = daysOfWeek.get(i);
            String dayOfWeekAsText = DateTimeUtil.getDayOfWeekAsText(DayOfWeek.values()[i]);
            dayOfWeekBtn.setText(dayOfWeekAsText);
            dayOfWeekBtn.setTextOn(dayOfWeekAsText);
            dayOfWeekBtn.setTextOff(dayOfWeekAsText);
        }
        //init
        for (DayOfWeek dayOfWeek : repeat.getDaysOfWeek()) {
            daysOfWeek.get(dayOfWeek.ordinal()).setChecked(true);
        }
        //onChange listeners
        for (int i = 0; i < daysOfWeek.size(); i++) {
            ToggleButton dayOfWeekBtn = daysOfWeek.get(i);
            final DayOfWeek dayOfWeek = DayOfWeek.values()[i];
            dayOfWeekBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed()) {
                        return;
                    }
                    if (isChecked) {
                        repeat.getDaysOfWeek().add(dayOfWeek);
                    } else {
                        repeat.getDaysOfWeek().remove(dayOfWeek);
                    }
                }
            });
        }
    }

    private void setUpTime() {
        updateTime();
        View startGroup = fragmentRoot.findViewById(R.id.weekly_custom_time_group);
        startGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                repeat.setTime(LocalTime.of(hour, minute));
                                updateTime();
                            }
                        },
                        repeat.getTime().getHour(), repeat.getTime().getMinute(), true)
                        .show();
            }
        });
    }

    private void updateTime() {
        hour.setText(repeat.getTime().format(HOUR_FORMATTER));
        minute.setText(repeat.getTime().format(MINUTE_FORMATTER));
    }
}
