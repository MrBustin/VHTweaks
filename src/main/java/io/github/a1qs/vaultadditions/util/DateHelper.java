package io.github.a1qs.vaultadditions.util;

import java.time.LocalDate;

public final class DateHelper {
    private DateHelper() {}

    public static boolean isAprilFools() {
        var d = LocalDate.now();
        return d.getMonthValue() == 4 && d.getDayOfMonth() == 1;
    }

}

