package feelings.guide.profile;

import android.content.Context;
import android.content.SharedPreferences;

import feelings.guide.R;
import feelings.guide.question.QuestionService;
import feelings.guide.util.ToastUtil;

public class LocaleChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;

    public LocaleChangeListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (LocaleUtil.SELECTED_LANGUAGE.equals(key)) {
            context = LocaleUtil.setLocale(context);
            QuestionService.changeLanguage(context);
            ToastUtil.showLong(context, context.getString(R.string.msg_change_language_restart));
        }
    }
}
