package feelings.helper.ui.schedule.fragments;

import android.app.TimePickerDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.TreeSet;

import feelings.helper.R;
import feelings.helper.ui.UiUtil;

import static feelings.helper.util.DateTimeUtil.HOUR_FORMATTER;
import static feelings.helper.util.DateTimeUtil.MINUTE_FORMATTER;

class DailyTimesAdapter extends RecyclerView.Adapter<DailyTimesAdapter.DailyTimeViewHolder> {

    private TreeSet<LocalTime> times;
    private boolean isOn;

    final class DailyTimeViewHolder extends RecyclerView.ViewHolder {
        LocalTime currentItem;
        TextView timeHour;
        TextView timeMinute;
        ImageView deleteIcon;

        private DailyTimeViewHolder(final View itemView) {
            super(itemView);
            timeHour = (TextView) itemView.findViewById(R.id.daily_time_hour);
            timeMinute = (TextView) itemView.findViewById(R.id.daily_time_minute);
            deleteIcon = (ImageView) itemView.findViewById(R.id.daily_time_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(itemView.getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hour, int minute) {
                                    times.remove(currentItem);
                                    currentItem = LocalTime.of(hour, minute);
                                    times.add(currentItem);
                                    // refresh all due to position might be changed
                                    notifyDataSetChanged();
                                }
                            },
                            currentItem.getHour(), currentItem.getMinute(), true)
                            .show();
                }
            });

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    times.remove(currentItem);
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            UiUtil.disableEnableControls(isOn, itemView);
        }
    }

    DailyTimesAdapter(TreeSet<LocalTime> times, boolean isOn) {
        this.times = times;
        this.isOn = isOn;
    }

    @Override
    public DailyTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_daily_repeat_time_item, parent, false);
        return new DailyTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyTimeViewHolder holder, int position) {
        LocalTime time = new ArrayList<>(times).get(position);
        holder.currentItem = time;

        holder.timeHour.setText(time.format(HOUR_FORMATTER));
        holder.timeMinute.setText(time.format(MINUTE_FORMATTER));
    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}
