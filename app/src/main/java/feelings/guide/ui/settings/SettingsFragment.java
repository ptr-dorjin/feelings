package feelings.guide.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.AlertDialog;

import feelings.guide.R;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.question.QuestionsActivity;
import feelings.guide.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragment {

    public static final String DATE_FORMAT_KEY = "Feelings.Guide.Date.Format";
    public static final String TIME_FORMAT_KEY = "Feelings.Guide.Time.Format";
    private static final String RESTORE_BUILT_IN_QUESTIONS_KEY = "Feelings.Guide.Restore.Built.In.Questions";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setUpDateFormatPreference();
        setUpTimeFormatPreference();
        setUpRestoreBuiltInQuestionsPreference();
    }

    private void setUpDateFormatPreference() {
        Preference dateFormatPreference = getPreferenceScreen().findPreference(DATE_FORMAT_KEY);
        dateFormatPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            Activity activity = getActivity();
            if (activity != null) {
                ToastUtil.showShort(activity, getString(R.string.msg_date_format_success));
            }
            return true;
        });
    }

    private void setUpTimeFormatPreference() {
        Preference dateFormatPreference = getPreferenceScreen().findPreference(TIME_FORMAT_KEY);
        dateFormatPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            Activity activity = getActivity();
            if (activity != null) {
                ToastUtil.showShort(activity, getString(R.string.msg_time_format_success));
            }
            return true;
        });
    }

    private void setUpRestoreBuiltInQuestionsPreference() {
        Preference clearLogPreference = getPreferenceScreen().findPreference(RESTORE_BUILT_IN_QUESTIONS_KEY);
        clearLogPreference.setOnPreferenceClickListener(preference -> {
            final Activity activity = getActivity();
            new AlertDialog.Builder(activity)
                    .setMessage(R.string.title_confirm_restore_built_in_questions_dialog)
                    .setPositiveButton(R.string.btn_restore, (dialog, id) -> {
                        QuestionService.restoreHidden(activity);
                        ToastUtil.showShort(activity, getString(R.string.msg_restore_built_in_questions_success));
                        Intent data = new Intent();
                        data.putExtra(QuestionsActivity.REFRESH_QUESTIONS_KEY, true);
                        activity.setResult(RESULT_OK, data);
                    })
                    .setNegativeButton(R.string.btn_cancel, (dialog, id) -> dialog.dismiss()).create()
                    .show();
            return true;
        });
    }
}
