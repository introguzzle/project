package ru.mathparser;

import java.util.*;

public final class MathFunctionParser {

    private MathFunctionParser() throws InstantiationException {
        throw new InstantiationException();
    }

    public static final class Parametric {

        private static final String VARIABLE = "t";

        private Parametric() throws InstantiationException {
            throw new InstantiationException();
        }

        public static boolean isFunction(final String parametricFunction) {
            return Parser.present(parametricFunction, VARIABLE) || Explicit.isNotFunction(parametricFunction);
        }

        private static String replaceVariable(final String parametricFunction,
                                              final double value) {
            return Parser.replace(parametricFunction, VARIABLE, value, 0, parametricFunction.length());
        }

        public static Set<String> getCoefficients(final String parametricFunction) {
            String f = MathParser.replaceConstants(parametricFunction);

            return Parser.find(f, List.of(VARIABLE), 0, f.length());
        }

        private static String replaceCoefficients(final String parametricFunction,
                                                  final Map<String, Double> coefficients) {
            return Explicit.replaceCoefficients(parametricFunction, coefficients);
        }

        public static String parse(final String parametricFunction,
                                   final Map<String, Double> coefficients,
                                   final double value) {
            String f1 = MathParser.replaceConstants(parametricFunction);
            String f2 = replaceCoefficients(f1, coefficients);

            return replaceVariable(f2, value);
        }

        public static boolean hasCoefficients(final String parametricFunction) {
            return !getCoefficients(parametricFunction).isEmpty();
        }

        public static boolean isValid(final String function) {
            try {
                String f = parse(function, bind(getCoefficients(function)), 1.0);

                MathParser.uncheckedParse(f);

            } catch (MathParserException e) {
                return false;
            }

            return true;
        }

        private static Map<String, Double> bind(Set<String> set) {
            Map<String, Double> map = new HashMap<>();

            for (var s: set)
                map.put(s, 1.0);

            return map;
        }
    }

    public static final class Implicit {


    }


    public static final class Explicit {

        private static final String NOT_FOUND = "-1";

        private Explicit() throws InstantiationError {
            throw new InstantiationError();
        }

        public static boolean isNotFunction(final String expression) {
            return getVariable(expression).equals(NOT_FOUND);
        }

        private static String cut(final String function) {
            return function.substring(eq(function) + 1);
        }

        private static int eq(String function) {
            return function.indexOf("=");
        }

        private static String getVariable(final String function) {
            int equalsSignIndex = eq(function);

            if (!function.substring(0, equalsSignIndex + 1).contains("(")
                    || !function.substring(0, equalsSignIndex + 1).contains(")"))
                return NOT_FOUND;

            Set<String> variable = Parser.find(function, List.of(), 0, equalsSignIndex);

            if (!variable.isEmpty())
                for (var v: variable)
                    return v;

            return NOT_FOUND;
        }

        private static String replaceVariable(final String function, final double value) {
            return Parser.replace(function, getVariable(function), value, eq(function) + 1, function.length());
        }

        public static Set<String> getCoefficients(final String function) {
            String f = MathParser.replaceConstants(function);

            return Parser.find(f, List.of(getVariable(f)), 0, f.length());
        }

        public static String parse(final String function,
                                   final Map<String, Double> coefficients,
                                   final double value) {
            String f1 = MathParser.replaceConstants(function);
            String f2 = replaceCoefficients(f1, coefficients);

            return cut(replaceVariable(f2, value));
        }

        private static String replaceCoefficients(final String function,
                                                  final Map<String, Double> coefficients) {
            String f = MathParser.replaceConstants(function);

            return Parser.replace(f, coefficients, 0, function.length());
        }

        public static boolean hasCoefficients(final String function) {
            return !getCoefficients(function).isEmpty();
        }

        public static boolean isValid(final String function) {
            try {
                String f = parse(function, bind(getCoefficients(function)), 1.0);
                String cut = cut(f);

                if (cut.isEmpty() || cut.isBlank())
                    return false;
                else
                    MathParser.uncheckedParse(cut);

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        private static Map<String, Double> bind(final Set<String> set) {
            Map<String, Double> map = new HashMap<>();
            double v = 1.0;

            for (var s: set)
                map.put(s, v);

            return map;
        }
    }
}
