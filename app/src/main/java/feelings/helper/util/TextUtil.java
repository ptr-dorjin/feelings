package feelings.helper.util;

import android.support.annotation.Nullable;

import java.util.Iterator;

public class TextUtil {

    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(@Nullable String str) {
        return isEmpty(str) || str.trim().isEmpty();
    }

    public static String getPluralText(int number, String one, String two, String five) {
        if (number > 10 && number < 20) {
            return five;
        }
        int mod = number % 10;
        if (mod == 1) {
            return one;
        } else if (mod >= 2 && mod <= 4) {
            return two;
        } else {
            return five;
        }
    }

}
