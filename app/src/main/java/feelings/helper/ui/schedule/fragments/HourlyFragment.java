package feelings.helper.ui.schedule.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.threeten.bp.LocalTime;

import feelings.helper.R;
import feelings.helper.repeat.HourlyRepeat;
import feelings.helper.repeat.Repeat;
import feelings.helper.repeat.RepeatType;
import feelings.helper.ui.UiUtil;
import feelings.helper.util.TextUtil;

import static feelings.helper.repeat.Repeat.HOUR_FORMATTER;
import static feelings.helper.repeat.Repeat.MINUTE_FORMATTER;
import static org.threeten.bp.LocalTime.of;

public class HourlyFragment extends AbstractFragment {

    private HourlyRepeat repeat;
    private View fragmentRoot;
    private TextView labelEvery;
    private TextView labelHour;
    private NumberPicker intervalPicker;
    private TextView startHour;
    private TextView startMinute;
    private TextView endHour;
    private TextView endMinute;

    @Override
    public String getFragmentTag() {
        return RepeatType.HOURLY.name();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!initSchedule()) {
            return null;
        }
        initRepeat();

        fragmentRoot = inflater.inflate(R.layout.fragment_hourly_repeat, container, false);
        labelEvery = (TextView) fragmentRoot.findViewById(R.id.label_every);
        labelHour = (TextView) fragmentRoot.findViewById(R.id.label_hour);
        intervalPicker = (NumberPicker) fragmentRoot.findViewById(R.id.hour_interval);
        startHour = (TextView) fragmentRoot.findViewById(R.id.start_hour);
        startMinute= (TextView) fragmentRoot.findViewById(R.id.start_minute);
        endHour = (TextView) fragmentRoot.findViewById(R.id.end_hour);
        endMinute = (TextView) fragmentRoot.findViewById(R.id.end_minute);

        setUpHourIntervalPicker();
        setUpStartTime();
        setUpEndTime();

        UiUtil.disableEnableControls(schedule.isOn(), fragmentRoot);
        return fragmentRoot;
    }

    private void initRepeat() {
        Repeat repeat = schedule.getRepeat();
        if (!(repeat instanceof HourlyRepeat)) {
            repeat = new HourlyRepeat(1, of(8, 0), of(20, 0));
            schedule.setRepeat(repeat);
        }
        this.repeat = (HourlyRepeat) repeat;
    }

    private void setUpHourIntervalPicker() {
        intervalPicker.setMinValue(1);
        intervalPicker.setMaxValue(24);
        intervalPicker.setValue(repeat.getInterval());
        intervalPicker.setOnValueChangedListener(intervalChangeListener);
        updateLabels(repeat.getInterval());
    }

    private NumberPicker.OnValueChangeListener intervalChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            repeat.setInterval(newVal);
            updateLabels(newVal);
        }
    };

    private void updateLabels(int newVal) {
        labelEvery.setText(TextUtil.getPluralText(newVal,
                HourlyFragment.this.getString(R.string.every1),
                HourlyFragment.this.getString(R.string.every2),
                HourlyFragment.this.getString(R.string.every5)));
        labelHour.setText(TextUtil.getPluralText(newVal,
                HourlyFragment.this.getString(R.string.hour1),
                HourlyFragment.this.getString(R.string.hour2),
                HourlyFragment.this.getString(R.string.hour5)));
    }

    private void setUpStartTime() {
        updateStartTime();
        View startGroup = fragmentRoot.findViewById(R.id.start_group);
        startGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                repeat.setStart(LocalTime.of(hour, minute));
                                updateStartTime();
                            }
                        },
                        repeat.getStart().getHour(), repeat.getStart().getMinute(), true)
                        .show();
            }
        });
    }

    private void setUpEndTime() {
        updateEndTime();
        View endGroup = fragmentRoot.findViewById(R.id.end_group);
        endGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                repeat.setEnd(LocalTime.of(hour, minute));
                                updateEndTime();
                            }
                        },
                        repeat.getEnd().getHour(), repeat.getEnd().getMinute(), true)
                        .show();
            }
        });
    }

    private void updateStartTime() {
        startHour.setText(repeat.getStart().format(HOUR_FORMATTER));
        startMinute.setText(repeat.getStart().format(MINUTE_FORMATTER));
    }

    private void updateEndTime() {
        endHour.setText(repeat.getEnd().format(HOUR_FORMATTER));
        endMinute.setText(repeat.getEnd().format(MINUTE_FORMATTER));
    }
}
