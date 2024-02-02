package ru.grapher;

import java.util.*;

public final class FunctionParsingUtilities {

    private FunctionParsingUtilities() throws InstantiationError {
        throw new InstantiationError();
    }

    public static boolean canBeConvertedToExplicit(final String function) {
        if (Implicit.eq(function) == -1)
            return false;

        String left     = Implicit.left(function).trim();
        String right    = Implicit.right(function).trim();

        boolean yboth   = SingleLetterUtilities.containsSingleLetter(left, "y") && SingleLetterUtilities.containsSingleLetter(right, "y");
        boolean xboth   = SingleLetterUtilities.containsSingleLetter(left, "x") && SingleLetterUtilities.containsSingleLetter(right, "x");

        if (left.equals(right) || xboth || yboth) {
            return false;
        }

        if (left.equals("y")) {
            return !SingleLetterUtilities.containsSingleLetter(right, "y");
        } else if (right.equals("y")) {
            return !SingleLetterUtilities.containsSingleLetter(left, "y");
        } else if (left.equals("x")) {
            return !SingleLetterUtilities.containsSingleLetter(right, "x");
        } else if (right.equals("x")) {
            return !SingleLetterUtilities.containsSingleLetter(left, "x");
        } else {
            return false;
        }
    }

    public static boolean isDeclaredImplicit(final String function) {
        return !function.contains("f(") || canBeConvertedToExplicit(function);
    }

    public static final class Parametric {

        private static final String VARIABLE = "t";

        private Parametric() throws InstantiationException {
            throw new InstantiationException();
        }

        public static boolean isFunction(String pfunction) {
            if (!Explicit.isFunction(pfunction))
                return SingleLetterUtilities.containsSingleLetter(pfunction, VARIABLE);
            else
                return false;
        }

        public static String replaceVariable(final String pfunction, final double value) {

            if (!(isFunction(pfunction))) {
                return pfunction;
            }

            String changed = Explicit.addVariableSplitters(pfunction, VARIABLE);

            return changed.replace(
                    Explicit.VARIABLE_SPLITTER + VARIABLE,
                    MathParser.Precision.evadeEFormat(value)
            );
        }

        public static HashSet<String> getCoefficients(final String pfunction) {

            String           changed         = MathParser.replaceConstants(Explicit.deleteDeclaration(pfunction));
            HashSet<String>  coefficients    = new HashSet<>();

            changed                          = Explicit.deleteFunctions(changed);

            StringBuilder    insertable      = new StringBuilder(changed);

            for (int i = 0; i < insertable.length(); i++) {
                String current = SingleLetterUtilities.letterAt(insertable, i);

                if (SingleLetterUtilities.isLetter(current) && !current.equals(VARIABLE))
                    coefficients.add(current);
            }

            return coefficients;
        }

        public static String replaceCoefficient(final String pfunction,
                                                final String coefficient,
                                                final double value) {

            StringBuilder changed = new StringBuilder(MathParser.replaceConstants(pfunction));

            int len = changed.length();

            for (int i = 0; i < len; i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);

                String left     = i != 0
                        ? SingleLetterUtilities.letterAt(changed, i - 1)
                        : " ";

                String right    = i != len - 1
                        ? SingleLetterUtilities.letterAt(changed, i + 1)
                        : " ";

                if (current.equals(coefficient) && SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right)) {
                    changed.insert(i, Explicit.COEFFICIENT_SPLITTER);
                }
            }

            return changed.toString().replace(
                    Explicit.COEFFICIENT_SPLITTER + coefficient,
                    MathParser.Precision.evadeEFormat(value)
            );
        }
    }

    private static final class SingleLetterUtilities {

        private SingleLetterUtilities() throws InstantiationError {
            throw new InstantiationError();
        }

        private static boolean containsSingleLetter(final String source,
                                                   final String target) {

            StringBuilder changed = new StringBuilder(source.trim());

            for (int i = 0; i < changed.length(); i++) {
                String current  = letterAt(changed, i);
                String left     = i != 0                    ? letterAt(changed, i - 1) : " ";
                String right    = i != changed.length() - 1 ? letterAt(changed, i + 1) : " ";

                if (isNotLetter(left) && isNotLetter(right) && current.equals(target))
                    return true;
            }

            return false;

        }

        private static boolean isLetter(final String single) {
            if (single.length() == 1)
                return Character.isLetter(single.charAt(0));
            else
                throw new IndexOutOfBoundsException("isLetter function exception");
        }

        private static boolean isNotLetter(final String single) {
            return !isLetter(single);
        }

        private static String letterAt(final String string, final int index) {
            return String.valueOf(string.charAt(index));
        }

        private static String letterAt(final StringBuilder string, final int index) {
            return String.valueOf(string.toString().charAt(index));
        }

        private static String trim(final String function) {
            return function.replace(" ", "");
        }

        private static String trim(final StringBuilder function) {
            return function.toString().replace(" ", "");
        }
    }

    public static final class Implicit {

        private Implicit() throws InstantiationError {
            throw new InstantiationError();
        }

        private static final String FIRST    = "x";
        private static final String SECOND   = "y";

        private static final String FS       = "X";
        private static final String SS       = "Y";

        public static boolean isFunction(final String function) {
            String changed = addVariableSplitters(function).trim();

            try {
                final int eq = eq(function);

                char ignored = function.charAt(eq);

                if (eq == -1) {
                    return false;
                }

            } catch (IndexOutOfBoundsException e) {
                return false;
            }

            return !(changed.length() == function.length()) && function.contains("=");
        }

        private static int eq(final String function) {
            return function.indexOf("=");
        }

        public static String replaceConstants(final String function) {
            return MathParser.replaceConstants(function);
        }

        public static String left(final String function) {
            if (!(eq(function) == -1))
                return function.substring(0, eq(function));
            else
                return function;
        }

        public static String right(final String function) {
            return function.substring(eq(function) + 1);
        }

        private static String addVariableSplitters(final String function) {

            StringBuilder changed = new StringBuilder(function);

            for (int i = 0; i < changed.length(); i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);
                String left     = i != 0
                        ? SingleLetterUtilities.letterAt(changed, i - 1)
                        : " ";

                String right    = i != changed.length() - 1
                        ? SingleLetterUtilities.letterAt(changed, i + 1)
                        : " ";

                if (current.equals(FIRST)) {

                    if (SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right))
                        changed.insert(i, FS);

                } else if (current.equals(SECOND)) {

                    if (SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right))
                        changed.insert(i, SS);
                }
            }

            return changed.toString();
        }

        public static String replaceX(final String function,
                                      final double x) {

            if (!isFunction(function)) {
                return function;
            }

            String changed = addVariableSplitters(function);

            return changed.replace(FIRST + FS, MathParser.Precision.evadeEFormat(x));
        }

        public static String replaceY(final String function,
                                      final double y) {

            if (!(isFunction(function))) {
                return function;
            }

            String changed = addVariableSplitters(function);

            return changed.replace(SECOND + SS, MathParser.Precision.evadeEFormat(y));
        }

        public static String inverse(final String function) {
            StringBuilder changed = new StringBuilder(function);

            for (int i = 0; i < changed.length(); i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);
                String left     = i != 0                    ? SingleLetterUtilities.letterAt(changed, i - 1) : " ";
                String right    = i != changed.length() - 1 ? SingleLetterUtilities.letterAt(changed, i + 1) : " ";

                if (current.equals(FIRST)) {

                    if (SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right))
                        changed.replace(i, i + 1, SECOND);

                } else if (current.equals(SECOND)) {

                    if (SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right))
                        changed.replace(i, i + 1, FIRST);
                }
            }

            return changed.toString();
        }
    }


    public static final class Explicit {

        private Explicit() throws InstantiationError {
            throw new InstantiationError();
        }

        private static final String         FUNCTION_DECLARATION    = "f(variable) = ...";
        private static final String         SIGNS_REGEX             = "[^a-zA-Z0-9]";

        private static final String VARIABLE_SPLITTER       = "VV";
        private static final String COEFFICIENT_SPLITTER    = "CC";

        public static boolean isFunction(final String expression) {
            return expression.matches(FUNCTION_DECLARATION) || !getVariable(expression).equals("-1");
        }

        public static ArrayList<Integer> indicesOfSingle(final String string, final String target) {
            int len = string.length();

            ArrayList<Integer> indices = new ArrayList<>();

            for (int i = 0; i < len; i++) {
                String current  = SingleLetterUtilities.letterAt(string, i);
                String left     = i != 0        ? SingleLetterUtilities.letterAt(string, i - 1) : " ";
                String right    = i != len - 1  ? SingleLetterUtilities.letterAt(string, i + 1) : " ";

                if (current.equals(target) && SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right)) {
                    indices.add(i);
                }
            }

            return indices;
        }

        private static String replace(final StringBuilder string,
                                      final String target,
                                      final String replacement) {
            return string.toString().replace(target, replacement);
        }

        private static String delete(final StringBuilder string,
                                     final String target) {
            return string.toString().replace(target, "");
        }

        private static String deleteDeclaration(final String function) {
            return new StringBuilder(SingleLetterUtilities.trim(function))
                    .substring(new StringBuilder(SingleLetterUtilities.trim(function)).indexOf("=") + 1);
        }

        private static String deleteDeclaration(final StringBuilder function) {
            return new StringBuilder(SingleLetterUtilities.trim(function))
                    .substring(new StringBuilder(SingleLetterUtilities.trim(function)).indexOf("=") + 1);
        }

        private static int countEntryOfSingle(final String string,
                                              final String single) {
            int entry = 0;

            String changed = deleteDeclaration(string);
            int len = changed.length();

            for (int i = 0; i < len; i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);
                String left     = i != 0        ? SingleLetterUtilities.letterAt(changed, i - 1) : " ";
                String right    = i != len - 1  ? SingleLetterUtilities.letterAt(changed, i + 1) : " ";

                if ((current.equals(single) && (SingleLetterUtilities.isNotLetter(left)) && (SingleLetterUtilities.isNotLetter(right))))
                    entry++;
            }

            return entry;
        }

        private static int countEntryOfSingle(final StringBuilder stringBuilder, final String single) {

            String  string      = stringBuilder.toString();
            int     entry       = 0;
            int     len         = string.length();

            for (int i = 0; i < len; i++) {
                String current  = SingleLetterUtilities.letterAt(string, i);
                String left     = i != 0        ? SingleLetterUtilities.letterAt(string, i - 1) : " ";
                String right    = i != len - 1  ? SingleLetterUtilities.letterAt(string, i + 1) : " ";

                if ((current.equals(single) && (SingleLetterUtilities.isNotLetter(left)) && (SingleLetterUtilities.isNotLetter(right))))
                    entry++;
            }

            return entry;
        }

        private static String deleteFunctions(final String function) {

            String changed = function;

            for (String name: MathFunctions.SORTED_NAMES) {
                changed = changed.replace(name, "");
//                if (changed.contains("arc"))
//                    changed = changed.replace("arc", "");
            }

            return changed;
        }

        public static String getVariable(final String function) {

            int equalsSignIndex         = function.indexOf("=");

            if (equalsSignIndex == -1)
                return "-1";

            String declaration = function.substring(0, equalsSignIndex);

            int leftBracketSignIndex    = declaration.indexOf("(");
            int rightBracketSignIndex   = declaration.indexOf(")");

            if (leftBracketSignIndex == -1 || rightBracketSignIndex == -1)
                return "-1";

            return SingleLetterUtilities.trim(declaration.substring(leftBracketSignIndex + 1, rightBracketSignIndex));
        }

        private static String addVariableSplitters(final String function, final String single) {

            StringBuilder changed = new StringBuilder(deleteDeclaration(function));

            for (int i = 0; i < changed.length(); i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);
                String left     = i != 0                    ? SingleLetterUtilities.letterAt(changed, i - 1) : " ";
                String right    = i != changed.length() - 1 ? SingleLetterUtilities.letterAt(changed, i + 1) : " ";

                if ((current.equals(single)) && (SingleLetterUtilities.isNotLetter(left)) && (SingleLetterUtilities.isNotLetter(right))) {
                    changed.insert(i, Explicit.VARIABLE_SPLITTER);
                }
            }

            return changed.toString();
        }

        public static String replaceVariable(final String function, final double value) {

            if (!(isFunction(function))) {
                return function;
            }

            String variable     = getVariable(function);
            String changed      = addVariableSplitters(function, variable);

            return changed.replace(VARIABLE_SPLITTER + variable, MathParser.Precision.evadeEFormat(value));
        }

        public static ArrayList<String> getCoefficients(final String function) {

            String           variable        = getVariable(function);
            String           changed         = MathParser.replaceConstants(deleteDeclaration(function));
            HashSet<String>  coefficients    = new HashSet<>();

            changed                          = deleteFunctions(changed);

            StringBuilder    insertable      = new StringBuilder(changed);

            for (int i = 0; i < insertable.length(); i++) {
                String current = SingleLetterUtilities.letterAt(insertable, i);

                if (SingleLetterUtilities.isLetter(current) && !current.equals(variable))
                    coefficients.add(current);
            }

            return new ArrayList<>(coefficients);
        }

        public static String replaceCoefficient(final String function,
                                                final String coefficient,
                                                final double value) {

            StringBuilder changed = new StringBuilder(MathParser.replaceConstants(deleteDeclaration(function)));

            int len = changed.length();

            for (int i = 0; i < len; i++) {
                String current  = SingleLetterUtilities.letterAt(changed, i);
                String left     = i != 0 ? SingleLetterUtilities.letterAt(changed, i - 1) : " ";
                String right    = i != len - 1 ? SingleLetterUtilities.letterAt(changed, i + 1) : " ";

                if (current.equals(coefficient) && SingleLetterUtilities.isNotLetter(left) && SingleLetterUtilities.isNotLetter(right)) {
                    changed.insert(i, COEFFICIENT_SPLITTER);
                }
            }

            return changed.toString().replace(COEFFICIENT_SPLITTER + coefficient, MathParser.Precision.evadeEFormat(value));
        }

        @Deprecated
        public static HashMap<String, List<Integer>> getCoeffs(String expression, String variable) {
            String _s = deleteDeclaration(expression);

            String _pre_string = deleteDeclaration(expression);
            _pre_string = MathParser.replaceConstants(_pre_string);
            StringBuilder _expression = new StringBuilder(expression);
            HashMap<String, List<Integer>> _coeffs = new HashMap<>();

            for (String name: MathFunctions.FUNCTION_MAP.keySet()) {
                _expression = new StringBuilder(_expression.toString().replace(name, ""));
            }

            _expression = new StringBuilder(_expression.toString().replace("arc", ""));

            _expression = new StringBuilder(_expression.toString().replace("E", ""));

            String[] _symbols = new String[] {"\\+", "\\-",
                    "\\*", "\\/",
                    "\\(", "\\)",
                    "\\,", "\\."};

            List<String> symbols = new ArrayList<>(Arrays.asList(_symbols));

            for (String s: symbols) {
                _expression = new StringBuilder(_expression.toString().replace(s, ""));
            }

            _expression = new StringBuilder(_expression.toString().replaceAll("[\\-\\+\\^:,]",""));

            for (int i = 0; i < _expression.length(); i++) {
                if (!(Character.isDigit(_expression.charAt(i))) & ((Character.isLetter(_expression.charAt(i))))) {
                    List<Integer> entries = new ArrayList<>();
                    for (int _i = 0; _i < _s.length(); _i++) {
                        char check_function_left = ' ';
                        char check_function_right = ' ';
                        if (_i != 0) {
                            check_function_left = _s.charAt(_i - 1);
                        }
                        if (_i != _s.length() - 1) {
                            check_function_right = _s.charAt(_i + 1);
                        }
                        if ((_s.charAt(_i) == _expression.charAt(i))
                                && (!(Character.isLetter(check_function_left)))
                                && (!(Character.isLetter(check_function_right)))
                            ) {
                            entries.add(_i);
                        }
                   }
                    _coeffs.put(Character.toString(_expression.charAt(i)), entries);
                }
            }

            return _coeffs;
        }

        @Deprecated
        public static HashMap<String, List<Integer>> getCoeffs(String expression) {
            String variable = getVariable(expression);
            boolean containsVariable = true;

            if (getVariable(expression).equals("-1")) {
                containsVariable = false;
            }


            String _s = deleteDeclaration(expression);
            StringBuilder _expression = new StringBuilder(deleteDeclaration(expression));
            HashMap<String, List<Integer>> _coeffs = new HashMap<>();

            for (String name: MathFunctions.FUNCTION_MAP.keySet()) {
                _expression = new StringBuilder(_expression.toString().replace(name, ""));
            }

            _expression = new StringBuilder(_expression.toString().replace("arc", ""));

            _expression = new StringBuilder(_expression.toString().replace("E", ""));

            String[] _symbols = new String[] {"\\+", "\\-",
                    "\\*", "\\/",
                    "\\(", "\\)",
                    "\\,", "\\."};

            List<String> symbols = new ArrayList<>(Arrays.asList(_symbols));

            for (String s: symbols) {
                _expression = new StringBuilder(_expression.toString().replace(s, ""));
            }

            _expression = new StringBuilder(_expression.toString().replaceAll("[\\-\\+\\^:,]",""));

            for (int i = 0; i < _expression.length(); i++) {
                if (!(Character.isDigit(_expression.charAt(i))) & ((Character.isLetter(_expression.charAt(i))))) {
                    List<Integer> entries = new ArrayList<>();
                    for (int _i = 0; _i < _s.length(); _i++) {
                        char check_function_left = ' ';
                        char check_function_right = ' ';
                        if (_i != 0) {
                            check_function_left = _s.charAt(_i - 1);
                        }
                        if (_i != _s.length() - 1) {
                            check_function_right = _s.charAt(_i + 1);
                        }
                        if ((_s.charAt(_i) == _expression.charAt(i))
                                && (!(Character.isLetter(check_function_left)))
                                && (!(Character.isLetter(check_function_right)))
                        ) {
                            entries.add(_i);
                        }
                    }
                    _coeffs.put(Character.toString(_expression.charAt(i)), entries);
                }
            }

            if (_coeffs.containsKey(variable) && containsVariable) {
                _coeffs.remove(variable);
            }

            return _coeffs;
        }

        @Deprecated
        public static String replaceCoeff(String expression, String coeff, double value) {
            String equation = deleteDeclaration(expression);
            equation = MathParser.replaceConstants(equation);
            StringBuilder _split = new StringBuilder(equation);
            int entry = 0;

            for (int i = 0; i < equation.length(); i++) {
                String current = Character.toString(_split.charAt(i));
                char left = (i != 0) ? _split.charAt(i - 1) : ' ';
                char right = (i != equation.length() - 1) ? _split.charAt(i + 1) : ' ';

                if ((current.equals(coeff)) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                    entry++;
                }
            }

            for (int i = 0; i < entry * 2; i++) {
                _split.append(" ");
            }

            for (int i = 0; i < equation.length() + entry * 2; i++) {
                String current = Character.toString(_split.charAt(i));
                char left = (i != 0) ? _split.charAt(i - 1) : ' ';
                char right = (i != equation.length() - 1) ? _split.charAt(i + 1) : ' ';

                if ((current.equals(coeff)) & (!Character.isLetter(left)) & (!Character.isLetter(right))) {
                    _split.insert(i, COEFFICIENT_SPLITTER);
                }
            }
            return _split.toString().replace(COEFFICIENT_SPLITTER + coeff, Double.toString(value));
        }
    }
}
