package feelings.helper.repetition;

import android.util.SparseArray;

import org.joda.time.LocalTime;

import java.util.Collection;
import java.util.TreeSet;

import feelings.helper.util.SparseArrayUtil;

import static feelings.helper.questions.QuestionService.DO_BODY;
import static feelings.helper.questions.QuestionService.FEELINGS;
import static feelings.helper.questions.QuestionService.GRATITUDE;
import static feelings.helper.questions.QuestionService.INSINCERITY;
import static feelings.helper.questions.QuestionService.PREACH;

public class RepetitionSettingStore {

    private static SparseArray<RepetitionSetting> repetitionSettings = new SparseArray<>();

    //delete
    static {
        int minute = 33;
        repetitionSettings.append(FEELINGS.getId(), new RepetitionSetting(FEELINGS.getId(), true,
                new HourlyRepetition(1, new LocalTime(8, minute), new LocalTime(23, 59))));
        repetitionSettings.append(INSINCERITY.getId(), new RepetitionSetting(INSINCERITY.getId(), true,
                createDailyRepetition(minute + 1)));
        repetitionSettings.append(GRATITUDE.getId(), new RepetitionSetting(GRATITUDE.getId(), true,
                createDailyRepetition(minute + 2)));
        repetitionSettings.append(DO_BODY.getId(), new RepetitionSetting(DO_BODY.getId(), true,
                createDailyRepetition(minute + 3)));
        repetitionSettings.append(PREACH.getId(), new RepetitionSetting(PREACH.getId(), true,
                createDailyRepetition(minute + 4)));
    }

    //todo save into the storage
    public static boolean saveRepetition(RepetitionSetting repetitionSetting) {
        return true;
    }

    //todo get from the storage
    public static RepetitionSetting getRepetitionSetting(int questionId) {
        RepetitionSetting repetitionSetting = repetitionSettings.get(questionId);
        if (repetitionSetting == null) {
            throw new RuntimeException("Unexpected questionId=" + questionId);
        }
        return repetitionSetting;
    }

    public static Collection<RepetitionSetting> getAllSettings() {
        return SparseArrayUtil.asCollection(repetitionSettings);
    }

    //delete
    private static Repetition createDailyRepetition(int minute) {
        TreeSet<LocalTime> times = new TreeSet<>();
        for (int i = 0; i < 24; i++) {
            times.add(new LocalTime(i, minute));
        }
        return new DailyRepetition(times);
    }
}
