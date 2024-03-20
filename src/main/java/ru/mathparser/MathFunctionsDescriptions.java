package ru.mathparser;

import java.util.HashMap;
import java.util.Map;

public final class MathFunctionsDescriptions {

    private static final Map<String, String> DESCRIPTIONS = new HashMap<>();

    public static Map<String, String> get() {
        return DESCRIPTIONS;
    }

    static {
        DESCRIPTIONS.put("abs",          "Absolute value(value).                     ");
        DESCRIPTIONS.put("pow",          "Exponentiation(base, power)                ");
        DESCRIPTIONS.put("cyclepow",     "Cycle exponentiation(values...)            ");

        DESCRIPTIONS.put("sin",          "sin(value)                                 ");
        DESCRIPTIONS.put("cos",          "cos(value)                                 ");
        DESCRIPTIONS.put("tg",           "tg(value)                                  ");
        DESCRIPTIONS.put("ctg",          "ctg(value)                                 ");

        DESCRIPTIONS.put("arcsin",       "arcsin(value)                              ");
        DESCRIPTIONS.put("arccos",       "arccos(value)                              ");
        DESCRIPTIONS.put("arctg",        "arctg(value)                               ");
        DESCRIPTIONS.put("arcctg",       "arcctg(value)                              ");

        DESCRIPTIONS.put("log",          "Logarithm(power, base)                     ");
        DESCRIPTIONS.put("ln",           "Natural logarithm(base)                    ");

        DESCRIPTIONS.put("sqrt",         "Square root(value)                         ");
        DESCRIPTIONS.put("cbrt",         "Cubic root(value)                          ");
        DESCRIPTIONS.put("nroot",        "N root(value, root power)                  ");

        DESCRIPTIONS.put("min",          "Minimal(list of values)                    ");
        DESCRIPTIONS.put("max",          "Maximal(list of values)                    ");

        DESCRIPTIONS.put("sq",           "Square(value)                              ");
        DESCRIPTIONS.put("cb",           "Cube(value)                                ");

        DESCRIPTIONS.put("sh",           "Hyberbolic sin(value)                      ");
        DESCRIPTIONS.put("ch",           "Hyberbolic cos(value)                      ");
        DESCRIPTIONS.put("th",           "Hyberbolic tg(value)                       ");
        DESCRIPTIONS.put("cth",          "Hyberbolic ctg(value)                      ");
        DESCRIPTIONS.put("sch",          "Hyberbolic sec(value)                      ");
        DESCRIPTIONS.put("csch",         "Hyberbolic cosec(value)                    ");

        DESCRIPTIONS.put("arsh",         "Area-function sh(value)                    ");
        DESCRIPTIONS.put("arch",         "Area-function ch(value)                    ");
        DESCRIPTIONS.put("arth",         "Area-function th(value)                    ");
        DESCRIPTIONS.put("arcth",        "Area-function cth(value)                   ");
        DESCRIPTIONS.put("arsch",        "Area-function sch(value)                   ");
        DESCRIPTIONS.put("arcsch",       "Area-function csch(value)                  ");

        DESCRIPTIONS.put("productlog",   "Lambert W-function(value)                  ");

        DESCRIPTIONS.put("randr",        "Random(min, max)                           ");
        DESCRIPTIONS.put("rand",         "Positive random(max)                       ");

        DESCRIPTIONS.put("gaussrand",    "Gauss random(mean, deviation)              ");
        DESCRIPTIONS.put("gaussdensity", "Normal distribution(value)                 ");
        DESCRIPTIONS.put("gaussdensityp","Normal distribution(value, mean, deviation)");

        DESCRIPTIONS.put("gamma",        "Gamma function(value)                      ");
        DESCRIPTIONS.put("digamma",      "Digamma function(value)                    ");
        DESCRIPTIONS.put("trigamma",     "Trigamma function(value)                   ");

        DESCRIPTIONS.put("erf",          "Error function(value)                      ");
        DESCRIPTIONS.put("erfinv",       "Inverse error function(value)              ");

    }
}
