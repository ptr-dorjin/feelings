package feelings.helper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import feelings.helper.alarm.AlarmService;
import feelings.helper.questions.QuestionService;
import feelings.helper.repetition.RepetitionSetting;
import feelings.helper.repetition.RepetitionSettingService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            QuestionService.init(context);
            for (RepetitionSetting repetitionSetting : RepetitionSettingService.getAllSettings()) {
                if (repetitionSetting.isOn()) {
                    AlarmService.setAlarm(context, repetitionSetting.getQuestionId());
                }
            }
        }
    }
}
