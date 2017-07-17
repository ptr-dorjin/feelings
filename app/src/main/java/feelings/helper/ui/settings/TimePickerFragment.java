package feelings.helper.ui.settings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import org.threeten.bp.LocalTime;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalTime now = LocalTime.now();
        return new TimePickerDialog(getActivity(), this, now.getHour(), now.getMinute(), true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }
}
