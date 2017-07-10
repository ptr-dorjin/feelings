package feelings.helper.ui.questions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import feelings.helper.questions.Question;
import feelings.helper.settings.Settings;

/**
 * Model for a question in a CardView
 */
class CardItem {
    @NonNull
    private Question question;
    @Nullable
    private Settings settings;

    CardItem(@NonNull Question question) {
        this.question = question;
    }

    CardItem(@NonNull Question question, Settings settings) {
        this.question = question;
        this.settings = settings;
    }

    @NonNull
    Question getQuestion() {
        return question;
    }

    @Nullable
    Settings getSettings() {
        return settings;
    }
}
