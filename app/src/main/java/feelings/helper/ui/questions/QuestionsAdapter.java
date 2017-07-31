package feelings.helper.ui.questions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import feelings.helper.R;
import feelings.helper.questions.Question;
import feelings.helper.questions.QuestionService;
import feelings.helper.schedule.Schedule;
import feelings.helper.schedule.ScheduleStore;
import feelings.helper.ui.answers.AnswerActivity;
import feelings.helper.ui.schedule.ScheduleActivity;
import feelings.helper.util.ToastUtil;

import static feelings.helper.ui.questions.QuestionsActivity.QUESTION_ID_PARAM;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private AppCompatActivity context;
    private List<CardItem> cardItems = new ArrayList<>();

    final class QuestionViewHolder extends RecyclerView.ViewHolder {
        CardItem currentItem;
        TextView questionText;
        TextView linkToAnswerNow;
        TextView repeat;
        Switch switchOnOff;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = (TextView) itemView.findViewById(R.id.question_text_on_card);
            linkToAnswerNow = (TextView) itemView.findViewById(R.id.link_to_answer_now);
            repeat = (TextView) itemView.findViewById(R.id.setup_repeat);
            switchOnOff = (Switch) itemView.findViewById(R.id.switchOnOff);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ScheduleActivity.class);
                    intent.putExtra(QUESTION_ID_PARAM, currentItem.getQuestion().getId());
                    // set position as a requestCode. This requestCode will be passed to this activity's onActivityResult
                    context.startActivityForResult(intent, getAdapterPosition());
                }
            });

            linkToAnswerNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AnswerActivity.class);
                    intent.putExtra(QUESTION_ID_PARAM, currentItem.getQuestion().getId());
                    context.startActivity(intent);
                }
            });

            switchOnOff.setOnCheckedChangeListener(listener);
            switchOnOff.setOnTouchListener(swipeListener);
        }

        private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                if (currentItem.getSchedule() == null) {
                    // do not set value
                    switchOnOff.setChecked(!isChecked);
                    // and go to schedule set up
                    Intent intent = new Intent(context, ScheduleActivity.class);
                    intent.putExtra(QUESTION_ID_PARAM, currentItem.getQuestion().getId());
                    context.startActivity(intent);
                } else {
                    ScheduleStore.switchOnOff(context, currentItem.getQuestion().getId(), isChecked);
                    ToastUtil.showShort(context.getString(isChecked
                            ? R.string.msg_repeat_switched_on
                            : R.string.msg_repeat_switched_off
                    ), context);
                }
            }
        };
    }

    QuestionsAdapter(AppCompatActivity context) {
        this.context = context;
        // The responsibility of this class is also to set question's schedules by values from the DB.
        Collection<Schedule> allSchedules = ScheduleStore.getAllSchedules(context);
        for (Question question : QuestionService.getAllQuestions(context)) {
            // find schedule in all schedules from the DB
            Schedule found = null;
            for (Schedule schedule : allSchedules) {
                if (schedule.getQuestionId() == question.getId()) {
                    found = schedule;
                    break;
                }
            }
            if (found != null) {
                cardItems.add(new CardItem(question, found));
            } else {
                cardItems.add(new CardItem(question));
            }
        }
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        bindCardItem(holder, cardItems.get(position));
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            // comes from ScheduleActivity on Save button pressed
            Schedule schedule = (Schedule) payloads.get(0);
            for (CardItem cardItem : cardItems) {
                if (cardItem.getQuestion().getId() == schedule.getQuestionId()) {
                    cardItem.setSchedule(schedule);
                    bindCardItem(holder, cardItem);
                    return;
                }
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }

    private void bindCardItem(QuestionViewHolder holder, CardItem cardItem) {
        Schedule schedule = cardItem.getSchedule();
        Context context = holder.questionText.getContext();

        holder.currentItem = cardItem;

        holder.questionText.setText(cardItem.getQuestion().getText());
        holder.repeat.setText(getHumanReadableRepeat(schedule, context));
        holder.switchOnOff.setChecked(schedule != null && schedule.isOn());
    }

    private CharSequence getHumanReadableRepeat(Schedule schedule, Context context) {
        String text = schedule != null
                ? schedule.getRepeat().toHumanReadableString(context)
                : context.getString(R.string.card_configure);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    private static final View.OnTouchListener swipeListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // need this listener to support swipe on the Switch
            return event.getActionMasked() == MotionEvent.ACTION_MOVE;
        }
    };

}
