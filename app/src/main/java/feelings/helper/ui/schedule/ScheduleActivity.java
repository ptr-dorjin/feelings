package feelings.helper.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import feelings.helper.R;
import feelings.helper.question.QuestionService;
import feelings.helper.repeat.DailyRepeat;
import feelings.helper.repeat.HourlyRepeat;
import feelings.helper.repeat.RepeatType;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleService;
import feelings.helper.ui.UiUtil;
import feelings.helper.ui.schedule.fragments.AbstractFragment;
import feelings.helper.util.ToastUtil;

import static feelings.helper.FeelingsApplication.QUESTION_ID_PARAM;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.create;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.fromPosition;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.getPosition;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.getTag;
import static feelings.helper.util.DateTimeUtil.DATE_TIME_TOAST_FORMATTER;
import static org.threeten.bp.LocalTime.of;

public class ScheduleActivity extends AppCompatActivity {
    public static final String SCHEDULE_PARCEL_KEY = "schedule-parcel";

    private Schedule schedule;
    private Spinner repeatTypeSpinner;
    private AbstractFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);

        int questionId = getIntent().getIntExtra(QUESTION_ID_PARAM, 0);

        TextView questionView = (TextView) findViewById(R.id.question_text_on_schedule);
        questionView.setText(QuestionService.getQuestionText(this, questionId));

        schedule = ScheduleService.getSchedule(this, questionId); //can be null
        if (schedule == null) {
            // no schedule in the DB => create it
            schedule = new Schedule(questionId, true, RepeatType.HOURLY, new HourlyRepeat(1, of(8, 0), of(20, 0)));
        }

        setUpSwitchOnOff();
        setUpRepeatTypeSpinner();
    }

    private void setUpSwitchOnOff() {
        Switch switchOnOff = (Switch) findViewById(R.id.switchOnOff_on_schedule);
        switchOnOff.setChecked(schedule.isOn());
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                schedule.setOn(isChecked);
                repeatTypeSpinner.setEnabled(isChecked);
                // this should disable almost all children
                UiUtil.disableEnableControls(isChecked, findViewById(R.id.fragment_container));
                // specific actions
                if (currentFragment != null) {
                    currentFragment.onSwitchOnOffChanged(isChecked);
                }
            }
        });
    }

    private void setUpRepeatTypeSpinner() {
        repeatTypeSpinner = (Spinner) findViewById(R.id.repeat_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_type_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        repeatTypeSpinner.setAdapter(adapter);
        repeatTypeSpinner.setOnItemSelectedListener(repeatTypeChangeListener);
        repeatTypeSpinner.setSelection(getPosition(schedule.getRepeatType()));
        repeatTypeSpinner.setEnabled(schedule.isOn());
    }

    private AdapterView.OnItemSelectedListener repeatTypeChangeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            FragmentManager fManager = getSupportFragmentManager();
            FragmentTransaction transaction = fManager.beginTransaction();

            currentFragment = (AbstractFragment) fManager.findFragmentByTag(getTag(pos));
            if (currentFragment == null) {
                currentFragment = create(pos, schedule);
                transaction.replace(R.id.fragment_container, currentFragment, currentFragment.getFragmentTag());
            } else {
                transaction.replace(R.id.fragment_container, currentFragment);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            //transaction.addToBackStack(null);
            schedule.setRepeatType(fromPosition(pos));
            transaction.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Save button pressed
        if (item.getItemId() == R.id.save_schedule) {
            if (isInvalid()) {
                return false;
            }
            boolean saved = ScheduleService.saveSchedule(this, schedule);
            if (saved) {
                // show message
                if (schedule.isOn()) {
                    LocalDateTime nextTime = schedule.getRepeat().getNextTime();
                    ToastUtil.showLong(this, String.format(
                            getString(R.string.msg_schedule_saved_on_success),
                            nextTime.format(DATE_TIME_TOAST_FORMATTER)));
                } else {
                    ToastUtil.showShort(this, getString(R.string.msg_schedule_saved_off_success));
                }
                // and send result to parent activity to refresh updated item on UI
                Intent data = new Intent();
                data.putExtra(SCHEDULE_PARCEL_KEY, schedule);
                setResult(RESULT_OK, data);
                finish();
            } else {
                ToastUtil.showLong(this, getString(R.string.msg_schedule_saved_error));
            }
            return saved;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Validation
     */
    private boolean isInvalid() {
        if (schedule.getRepeatType() == RepeatType.DAILY) {
            DailyRepeat dailyRepeat = (DailyRepeat) schedule.getRepeat();
            if (dailyRepeat.getTimes().isEmpty()) {
                ToastUtil.showLong(this, getString(R.string.msg_daily_repeat_empty));
                return true;
            }
        }
        return false;
    }
}
