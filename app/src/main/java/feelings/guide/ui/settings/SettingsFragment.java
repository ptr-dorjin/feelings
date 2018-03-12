package feelings.guide.ui.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import feelings.guide.R;
import feelings.guide.answer.AnswerStore;
import feelings.guide.question.QuestionService;
import feelings.guide.ui.question.QuestionsActivity;
import feelings.guide.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragment {

    private static final String CLEAR_LOG_FULL_KEY = "Feelings.Guide.Clear.Log.Full";
    private static final String CLEAR_LOG_DELETED_KEY = "Feelings.Guide.Clear.Log.Deleted";
    private static final String RESTORE_BUILT_IN_QUESTIONS_KEY = "Feelings.Guide.Restore.Built.In.Questions";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setUpClearLogFullPreference();
        setUpClearLogDeletedPreference();
        setUpRestoreBuiltInQuestionsPreference();
    }

    private void setUpClearLogFullPreference() {
        Preference clearLogPreference = getPreferenceScreen().findPreference(CLEAR_LOG_FULL_KEY);
        clearLogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final Activity activity = getActivity();
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.title_confirm_clear_log_full_dialog)
                        .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AnswerStore.deleteAll(activity);
                                ToastUtil.showShort(activity, getString(R.string.msg_clear_log_full_success));
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
                return true;
            }
        });
    }

    private void setUpClearLogDeletedPreference() {
        Preference clearLogPreference = getPreferenceScreen().findPreference(CLEAR_LOG_DELETED_KEY);
        clearLogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final Activity activity = getActivity();
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.title_confirm_clear_log_deleted_dialog)
                        .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AnswerStore.deleteForDeletedQuestions(activity);
                                ToastUtil.showShort(activity, getString(R.string.msg_clear_log_full_success));
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
                return true;
            }
        });
    }

    private void setUpRestoreBuiltInQuestionsPreference() {
        Preference clearLogPreference = getPreferenceScreen().findPreference(RESTORE_BUILT_IN_QUESTIONS_KEY);
        clearLogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final Activity activity = getActivity();
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.title_confirm_restore_built_in_questions_dialog)
                        .setPositiveButton(R.string.btn_restore, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                QuestionService.restoreHidden(activity);
                                ToastUtil.showShort(activity, getString(R.string.msg_restore_built_in_questions_success));
                                Intent data = new Intent();
                                data.putExtra(QuestionsActivity.REFRESH_QUESTIONS_KEY, true);
                                activity.setResult(RESULT_OK, data);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
                return true;
            }
        });
    }
}
