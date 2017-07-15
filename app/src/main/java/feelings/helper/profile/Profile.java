package feelings.helper.profile;

import java.util.Locale;

public class Profile {
    private static final Locale RU = new Locale("ru");

    public static Locale getLocale() {
        // todo get from settings
        return RU;
    }
}
