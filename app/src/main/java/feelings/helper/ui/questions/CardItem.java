package feelings.helper.ui.questions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import feelings.helper.questions.Question;
import feelings.helper.schedule.Schedule;

/**
 * Model for a question in a CardView
 */
class CardItem {
    @NonNull
    private Question question;
    @Nullable
    private Schedule schedule;

    CardItem(@NonNull Question question) {
        this.question = question;
    }

    CardItem(@NonNull Question question, Schedule schedule) {
        this.question = question;
        this.schedule = schedule;
    }

    @NonNull
    Question getQuestion() {
        return question;
    }

    @Nullable
    Schedule getSchedule() {
        return schedule;
    }
}
