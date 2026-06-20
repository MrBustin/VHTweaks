package io.github.a1qs.vaultadditions.util;

import iskallia.vault.util.data.WeightedList;

public class EntryHelper {
    public static <T> int findIndexOf(WeightedList<T> weightedList, T target) {
        for (int i = 0; i < weightedList.size(); i++) {
            WeightedList.Entry<T> entry = weightedList.get(i);
            if (entry.value.equals(target)) {
                return i;
            }
        }
        throw new IllegalStateException("Could not find index of the given target");
    }
}
