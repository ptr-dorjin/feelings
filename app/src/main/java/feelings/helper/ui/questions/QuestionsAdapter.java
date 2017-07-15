package feelings.helper.ui.questions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import feelings.helper.R;
import feelings.helper.questions.Question;
import feelings.helper.questions.QuestionService;
import feelings.helper.settings.Settings;
import feelings.helper.settings.SettingsStore;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private List<CardItem> cardItems = new ArrayList<>();

    static final class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView repeat;
        Switch switchOnOff;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = (TextView) itemView.findViewById(R.id.question_text);
            repeat = (TextView) itemView.findViewById(R.id.repeat);
            switchOnOff = (Switch) itemView.findViewById(R.id.switchOnOff);
        }
    }

    public QuestionsAdapter(Context context) {
        // The responsibility of this class is also to set question's repeat settings by values from the DB.
        Collection<Settings> allSettings = SettingsStore.getAllSettings(context);
        for (Question question : QuestionService.getAllQuestions(context)) {
            // find settings in all settings from the DB
            Settings found = null;
            for (Settings settings : allSettings) {
                if (settings.getQuestionId() == question.getId()) {
                    found = settings;
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
    public void onBindViewHolder(QuestionViewHolder holder, int i) {
        CardItem cardItem = cardItems.get(i);
        Settings settings = cardItem.getSettings();
        Context context = holder.questionText.getContext();

        holder.questionText.setText(cardItem.getQuestion().getText());
        holder.repeat.setText(settings != null
                ? settings.getRepeat().toString()
                : context.getString(R.string.not_configured));
        holder.switchOnOff.setEnabled(settings != null);
        holder.switchOnOff.setChecked(settings != null && settings.isOn());
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }
}
