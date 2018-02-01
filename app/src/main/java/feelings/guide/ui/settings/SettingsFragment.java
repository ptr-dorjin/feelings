package feelings.guide.ui.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import feelings.guide.R;
import feelings.guide.answer.AnswerStore;
import feelings.guide.util.ToastUtil;

public class SettingsFragment extends PreferenceFragment {

    private static final String CLEAR_LOG_KEY = "Feelings.Guide.Clear.Log";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setUpClearLogPreference();
    }

    private void setUpClearLogPreference() {
        Preference clearLogPreference = getPreferenceScreen().findPreference(CLEAR_LOG_KEY);
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
}
