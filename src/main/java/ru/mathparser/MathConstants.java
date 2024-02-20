package ru.mathparser;

import java.util.Map;

import static java.util.Map.entry;

public interface MathConstants {

    String PREFIX = "M";

    // gr is the golden ratio
    // sgr is the super-golden ratio
    // em is the euler-mascheroni constant

    Map<String, Double> MATH_CONSTANTS = Map.ofEntries(
            entry("pi"  , Math.PI),
            entry("e"   , Math.E ),
            entry("gr"  , 1.61803),
            entry("sgr" , 1.46557),
            entry("em"  , 0.57721)
    );

    static String replaceConstants(String expression) {
        throw new UnsupportedOperationException();
    }
}
