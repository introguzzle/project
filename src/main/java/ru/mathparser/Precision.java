package ru.mathparser;

import java.text.DecimalFormat;

public final class Precision {

    private static final DecimalFormat NUMBER_FORMAT    =
            new DecimalFormat("#.#############################################");

    private Precision() throws InstantiationException {
        throw new InstantiationException();
    }

    private static final int[] POW10 = {1, 10, 100, 1000, 10000, 100000, 1000000};

    public static String format(final double value, final int precision) {
        StringBuilder result = new StringBuilder();
        double v = value;

        if (v < 0) {
            result.append('-');
            v = -v;
        }

        int exp = POW10[precision];
        long lvalue = (long) (v * exp + 0.5);

        result.append(lvalue / exp).append('.');

        long fvalue = lvalue % exp;

        for (int p = precision - 1; p > 0 && fvalue < POW10[p]; p--) {
            result.append('0');
        }

        result.append(fvalue);

        return result.toString();
    }

    public static String format(double value) {
        return NUMBER_FORMAT.format(value).replace(",", ".");
    }
}
