package feelings.helper.ui.schedule.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import feelings.helper.R;
import feelings.helper.repeat.Repeat;
import feelings.helper.repeat.RepeatType;
import feelings.helper.repeat.WeeklyRepeat;
import feelings.helper.ui.UiUtil;
import feelings.helper.util.DateTimeUtil;

import static feelings.helper.util.DateTimeUtil.HOUR_FORMATTER;
import static feelings.helper.util.DateTimeUtil.MINUTE_FORMATTER;

public class WeeklyFragment extends AbstractFragment {

    private WeeklyRepeat repeat;
    private View fragmentRoot;
    private TextView hour;
    private TextView minute;
    private Spinner dayOfWeekSpinner;

    @Override
    public String getFragmentTag() {
        return RepeatType.WEEKLY.name();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!initSchedule()) {
            return null;
        }
        initRepeat();

        fragmentRoot = inflater.inflate(R.layout.fragment_weekly_repeat, container, false);
        dayOfWeekSpinner = (Spinner) fragmentRoot.findViewById(R.id.day_of_week_spinner);
        hour = (TextView) fragmentRoot.findViewById(R.id.weekly_time_hour);
        minute = (TextView) fragmentRoot.findViewById(R.id.weekly_time_minute);

        setUpRepeatTypeSpinner();
        setUpTime();

        UiUtil.disableEnableControls(schedule.isOn(), fragmentRoot);
        return fragmentRoot;
    }

    private void initRepeat() {
        Repeat repeat = schedule.getRepeat();
        if (!(repeat instanceof WeeklyRepeat)) {
            repeat = new WeeklyRepeat(DayOfWeek.MONDAY, LocalTime.of(8, 0));
            schedule.setRepeat(repeat);
        }
        this.repeat = (WeeklyRepeat) repeat;
    }

    private void setUpRepeatTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                DateTimeUtil.getAllDaysOfWeekLocalized());
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);
        dayOfWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeat.setDayOfWeek(DayOfWeek.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        dayOfWeekSpinner.setSelection(repeat.getDayOfWeek().ordinal());
    }

    private void setUpTime() {
        updateTime();
        View startGroup = fragmentRoot.findViewById(R.id.weekly_time_group);
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
