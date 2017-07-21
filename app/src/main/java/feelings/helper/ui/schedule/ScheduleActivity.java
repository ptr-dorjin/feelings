package feelings.helper.ui.schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import feelings.helper.R;
import feelings.helper.questions.QuestionService;
import feelings.helper.repeat.HourlyRepeat;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleStore;

import static feelings.helper.ui.questions.QuestionsActivity.QUESTION_ID_PARAM;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.create;
import static feelings.helper.ui.schedule.fragments.FragmentFactory.getTag;
import static org.threeten.bp.LocalTime.of;

public class ScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Schedule schedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);

        int questionId = getIntent().getIntExtra(QUESTION_ID_PARAM, 0);

        TextView questionView = (TextView) findViewById(R.id.question_text_on_schedule);
        questionView.setText(QuestionService.getQuestionText(this, questionId));

        schedule = ScheduleStore.getSchedule(this, questionId);
        if (schedule == null) {
            // no schedule in the DB => create it
            schedule = new Schedule(questionId, true, new HourlyRepeat(1, of(8, 0), of(20, 0)));
        }
        setUpRepeatTypeSpinner();
    }

    private void setUpRepeatTypeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.repeat_type_spinner);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();

        Fragment fragment = fManager.findFragmentByTag(getTag(pos));
        if (fragment == null) {
            fragment = create(pos);
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }}
