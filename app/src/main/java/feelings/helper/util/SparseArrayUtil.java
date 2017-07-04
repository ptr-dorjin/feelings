package feelings.helper.util;

import android.util.SparseArray;

import java.util.Collection;
import java.util.HashSet;

public class SparseArrayUtil {

    public static <C> Collection<C> asCollection(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        Collection<C> collection = new HashSet<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            collection.add(sparseArray.valueAt(i));
        return collection;
    }
}
