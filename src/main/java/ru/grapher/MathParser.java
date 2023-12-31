package ru.grapher;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;

public final class MathParser {
    public static final class SyntaxParseException extends Exception {
        public SyntaxParseException() {
            super();
        }
        public SyntaxParseException(String message) {
            super(message);
        }
        public SyntaxParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private MathParser() throws ClassNotFoundException {
        throw new ClassNotFoundException();
    }

    /*------------------------------------------------------------------
    * PARSER RULES
    *-----------------------------------------------------------------*/

//    EXPRESSION : ADD_SUBTRACT* EOF ;
//
//    ADD_SUBTRACT: MULTIPLY_DIVIDE ( ( '+' | '-' ) MULTIPLY_DIVIDE )* ;
//
//    MULTIPLY_DIVIDE : FACTOR ( ( '*' | '/' ) FACTOR )* ;
//
//    FACTOR : FUNCTION | UNARY | NUMBER | '(' EXPRESSION ')' ;
//
//    UNARY: '-' FACTOR
//
//    FUNCTION: NAME '(' EXPRESSION(, EXPRESSION)+)? ')'
//
//

    private static final HashMap<String, MathFunction> FUNCTION_MAP = MathFunctions.getFunctionMap();

    public static double parse(final String expression) {

        List<Lexeme> lexemeList = null;
        try {
            lexemeList = MathParser.lexParse(FunctionHandler.replaceConstants(expression));
        } catch (SyntaxParseException e) {
            Graph.getLogger().log(Level.SEVERE, "wrong expression", e);
        }

        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemeList);

        try {
            return Syntax.expression(lexemeBuffer);
        } catch (SyntaxParseException e) {
            Graph.getLogger().log(Level.SEVERE, "wrong expression", e);
        }

        return 0;
    }

    public static double compute(final String function, final double value, final HashMap<String, Double> map) {
        return 0;
    }

    public static double[][] threadedGetData() {
        return new double[][]{};
    }

    public static class Lexeme {
        public LexemeType lexemeType;
        public String string;

        public Lexeme(LexemeType lexemeType, String string) {
            this.lexemeType = lexemeType;
            this.string = string;
        }

        public Lexeme(LexemeType lexemeType, Character character) {
            this.lexemeType = lexemeType;
            this.string = character.toString();
        }

        public LexemeType getLexemeType() {
            return this.lexemeType;
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + lexemeType +
                    ", string='" + string + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }

    }

    public static List<Lexeme> lexParse(String expression) throws SyntaxParseException {
        ArrayList<Lexeme> lexemeList = new ArrayList<>();
        int pos = 0;

        while (pos < expression.length()) {

            char symbol = expression.charAt(pos);
            switch (symbol) {
                case '(':
                    lexemeList.add(new Lexeme(LexemeType.LEFT, symbol));
                    pos++;
                    continue;
                case ')':
                    lexemeList.add(new Lexeme(LexemeType.RIGHT, symbol));
                    pos++;
                    continue;
                case '+':
                    lexemeList.add(new Lexeme(LexemeType.OPERATOR_ADD, symbol));
                    pos++;
                    continue;
                case '-':
                    lexemeList.add(new Lexeme(LexemeType.OPERATOR_SUB, symbol));
                    pos++;
                    continue;
                case '*':
                    lexemeList.add(new Lexeme(LexemeType.OPERATOR_MUL, symbol));
                    pos++;
                    continue;
                case '/':
                    lexemeList.add(new Lexeme(LexemeType.OPERATOR_DIV, symbol));
                    pos++;
                    continue;
                case ',':
                    lexemeList.add(new Lexeme(LexemeType.COMMA, symbol));
                    pos++;
                    continue;
                default:
                    if ((symbol <= '9' && symbol >= '0') | (symbol == '.')) {

                        StringBuilder number = new StringBuilder();
                        do {
                            number.append(symbol);
                            pos++;
                            if (pos >= expression.length())
                                break;
                            symbol = expression.charAt(pos);
                        } while ((symbol <= '9' && symbol >= '0') | (symbol == '.'));
                        lexemeList.add(new Lexeme(LexemeType.NUMBER, number.toString()));

                    } else {
                        if (symbol != ' ') {
                            if ((symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z')) {

                                StringBuilder function = new StringBuilder();

                                do {
                                    function.append(symbol);
                                    pos++;
                                    if (pos >= expression.length())
                                        break;
                                    symbol = expression.charAt(pos);
                                } while ((symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z'));

                                if (FUNCTION_MAP.containsKey(function.toString())) {
                                    lexemeList.add(new Lexeme(LexemeType.FUNCTION_NAME, function.toString()));

                                } else {
                                    throw new SyntaxParseException();
                                }

                            } else {
                                throw new SyntaxParseException("Unexpected character: '" + symbol + "' at pos " + pos + " in expression");
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }

        lexemeList.add(new Lexeme(LexemeType.EOF, ""));

        return lexemeList;
    }

    public static final class Precision {

        private Precision() throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        private static final int[] POW10 = {1, 10, 100, 1000, 10000, 100000, 1000000};

        public static final String _format(final double argValue, final int precision) {
            StringBuilder result = new StringBuilder();
            double value = argValue;

            if (value < 0) {
                result.append('-');
                value = -value;
            }

            int exp = POW10[precision];
            long lvalue = (long)(value * exp + 0.5);

            result.append(lvalue / exp).append('.');

            long fvalue = lvalue % exp;

            for (int p = precision - 1; p > 0 && fvalue < POW10[p]; p--) {
                result.append('0');
            }

            result.append(fvalue);

            return result.toString();
        }

        public static final String evadeEFormat(double value)
        {
            return new DecimalFormat("0.0000000000").format(value).replace(",",".");
        }
    }

    public static final class Syntax {

        private Syntax() throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        public static double expression(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            Lexeme lexeme = lexemeBuffer.next();
            if (lexeme.lexemeType == LexemeType.EOF) {
                return 0;
            } else {
                lexemeBuffer.back();
                return addSubtract(lexemeBuffer);
            }
        }

        public static double addSubtract(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            double value = multiplyDivide(lexemeBuffer);
            while (true) {
                Lexeme lexeme = lexemeBuffer.next();
                switch (lexeme.lexemeType) {
                    case OPERATOR_ADD:
                        value += multiplyDivide(lexemeBuffer);
                        break;
                    case OPERATOR_SUB:
                        value -= multiplyDivide(lexemeBuffer);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                        lexemeBuffer.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getLexemeType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                }
            }
        }

        public static double multiplyDivide(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            double value = factor(lexemeBuffer);
            while (true) {
                Lexeme lexeme = lexemeBuffer.next();
                switch (lexeme.lexemeType) {
                    case OPERATOR_MUL:
                        value *= factor(lexemeBuffer);
                        break;
                    case OPERATOR_DIV:
                        value /= factor(lexemeBuffer);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                    case OPERATOR_ADD:
                    case OPERATOR_SUB:
                        lexemeBuffer.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getLexemeType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                }
            }
        }

        public static double factor(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            Lexeme lexeme = lexemeBuffer.next();
            switch (lexeme.lexemeType) {
                case FUNCTION_NAME:
                    lexemeBuffer.back();
                    return function(lexemeBuffer);

                case OPERATOR_SUB:
                    double positiveValue = factor(lexemeBuffer);
                    return -positiveValue;

                case NUMBER:
                    try {
                        return Double.parseDouble(lexeme.string);
                    } catch (NumberFormatException e) {
                        Graph.getLogger().log(Level.SEVERE, "number format exception", e);
                    }

                case LEFT:
                    double value = expression(lexemeBuffer);
                    lexeme = lexemeBuffer.next();
                    if (lexeme.lexemeType != LexemeType.RIGHT) {
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getLexemeType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                    }
                    return value;

                default:
                    throw new SyntaxParseException("Unexpected token: '" + lexeme.getLexemeType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
            }
        }

        public static double function(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            String function = lexemeBuffer.next().string;
            Lexeme lexeme = lexemeBuffer.next();

            if (lexeme.lexemeType != LexemeType.LEFT) {
                throw new SyntaxParseException();
            }

            ArrayList<Double> args = new ArrayList<>();
            lexeme = lexemeBuffer.next();

            if (lexeme.lexemeType != LexemeType.RIGHT) {
                lexemeBuffer.back();
                do {

                    args.add(expression(lexemeBuffer));
                    lexeme = lexemeBuffer.next();

                    if ((lexeme.lexemeType != LexemeType.COMMA) && (lexeme.lexemeType != LexemeType.RIGHT)) {
                        throw new SyntaxParseException();
                    }

                } while (lexeme.lexemeType == LexemeType.COMMA);
            }
            return FUNCTION_MAP.get(function).apply(args);
        }
    }

    public static final class FunctionHandler {

        private FunctionHandler() throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        private static final String FUNCTION_DECLARATION = "f(variable) = ...";
        private static final String SIGNS_REGEX = "[^a-zA-Z0-9]";

        private static final String MATH_CONSTANTS_PREFIX = "mp_";
        private static final List<String> MATH_CONSTANTS_NAMES = Arrays.asList(MATH_CONSTANTS_PREFIX + "pi",
                MATH_CONSTANTS_PREFIX + "e",
                MATH_CONSTANTS_PREFIX + "phi",
                MATH_CONSTANTS_PREFIX + "m"
        );
        private static final List<Double> MATH_CONSTANTS_VALUES = Arrays.asList(3.14592, 2.71828, 1.61803, 0.57721);
        private static final String VARIABLE_SPLITTER = "VV";
        private static final String COEFFICIENT_SPLITTER = "CC";

        // phi is the golden ratio
        // m is the euler-mascheroni constant

//        private static final HashMap<String, Double> MATH_CONSTANTS = new HashMap<>() {{
//            MATH_CONSTANTS.put(_PI, _PI_VALUE);
//
//        }};

        private static final HashMap<String, Double> MATH_CONSTANTS = zip();

        private static HashMap<String, Double> zip() {
            try {
                HashMap<String, Double> map = new HashMap<>();
                for (int i = 0; i < MATH_CONSTANTS_NAMES.size(); i++) {
                    map.put(MATH_CONSTANTS_NAMES.get(i), MATH_CONSTANTS_VALUES.get(i));
                }
                return map;
            } catch (IndexOutOfBoundsException e) {
                Graph.getLogger().log(Level.SEVERE, "", e);
            }
            return new HashMap<>();
        }

        private static boolean isFunction(final String expression) {
            return expression.matches(FUNCTION_DECLARATION) || !getVariable(expression).equals("-1");
        }

        public static final ArrayList<Integer> indicesOfSingle(final String string, final String target) {
            int len = string.length();

            ArrayList<Integer> indices = new ArrayList<>();

            for (int i = 0; i < len; i++) {
                String current = singleAt(string, i);
                String left =
                        i != 0 ? singleAt(string, i - 1) : " ";
                String right =
                        i != len - 1 ? singleAt(string, i + 1) : " ";

                if (current.equals(target) && isNotLetter(left) && isNotLetter(right)) {
                    indices.add(i);
                }
            }

            return indices;
        }

        private static String replace(final StringBuilder string, final String target, final String replacement) {
            return string.toString().replace(target, replacement);
        }

        private static String delete(final StringBuilder string, final String target) {
            return string.toString().replace(target, "");
        }

        private static String deleteSpaces(final String function) {
            return function.replace(" ", "");
        }

        private static String deleteSpaces(final StringBuilder function) {
            return function.toString().replace(" ", "");
        }

        private static String deleteDeclaration(final String function) {
            return new StringBuilder(deleteSpaces(function))
                    .substring(new StringBuilder(deleteSpaces(function)).indexOf("=") + 1);
        }

        private static String deleteDeclaration(final StringBuilder function) {
            return new StringBuilder(deleteSpaces(function))
                    .substring(new StringBuilder(deleteSpaces(function)).indexOf("=") + 1);
        }

        private static boolean isLetter(final String single) {
            return !isNotLetter(single);
        }

        private static boolean isNotLetter(final String single) {
            if (single.length() == 1)
                return !Character.isLetter(single.charAt(0));
            else
                throw new IndexOutOfBoundsException("isNotLetter function exception");
        }

        private static String singleAt(final String string, final int index) {
            return String.valueOf(string.charAt(index));
        }

        private static String singleAt(final StringBuilder string, final int index) {
            return String.valueOf(string.toString().charAt(index));
        }

        private static int countEntryOfSingle(final String string, final String single) {
            int entry = 0;

            String changed = deleteDeclaration(string);
            int len = changed.length();

            for (int i = 0; i < len; i++) {
                String current = singleAt(changed, i);
                String left =
                        i != 0 ? singleAt(changed, i - 1) : " ";
                String right =
                        i != len - 1 ? singleAt(changed, i + 1) : " ";

                if ((current.equals(single) && (isNotLetter(left)) && (isNotLetter(right))))
                    entry++;
            }

            return entry;
        }

        private static int countEntryOfSingle(final StringBuilder stringBuilder, final String single) {
            String string = stringBuilder.toString();
            int entry = 0;
            int len = string.length();

            for (int i = 0; i < len; i++) {
                String current = singleAt(string, i);
                String left =
                        i != 0 ? singleAt(string, i - 1) : " ";
                String right =
                        i != len - 1 ? singleAt(string, i + 1) : " ";

                if ((current.equals(single) && (isNotLetter(left)) && (isNotLetter(right))))
                    entry++;
            }

            return entry;
        }

        public static final String deleteFunctions(final String function) {
            String changed = function;

            for (String mathFunctionName: FUNCTION_MAP.keySet()) {
                changed = changed.replace(mathFunctionName, "");
                if (changed.contains("arc"))
                    changed = changed.replace("arc", "");
            }

            return changed;
        }

        public static final String getVariable(final String function) {
            int equalsSignIndex = function.indexOf("=");
            if (equalsSignIndex == -1)
                return "-1";

            String declaration = function.substring(0, equalsSignIndex);

            int leftBracketSignIndex = declaration.indexOf("(");
            int rightBracketSignIndex = declaration.indexOf(")");

            if (leftBracketSignIndex == -1 || rightBracketSignIndex == -1)
                return "-1";

            return deleteSpaces(declaration.substring(leftBracketSignIndex + 1, rightBracketSignIndex));
        }

        private static String addSplitters(final String function, final String single, final String splitter) {
            StringBuilder changed = new StringBuilder(deleteDeclaration(function));

            for (int i = 0; i < changed.length(); i++) {
                String current = singleAt(changed, i);
                String left =
                        i != 0 ? singleAt(changed, i - 1) : " ";
                String right =
                        i != changed.length() - 1 ? singleAt(changed, i + 1) : " ";

                if ((current.equals(single)) && (isNotLetter(left)) && (isNotLetter(right))) {
                    changed.insert(i, splitter);
                }
            }

            return changed.toString();
        }

        public static final String replaceVariable(final String function, final double value) {
            if (!(isFunction(function))) {
                return function;
            }

            String variable = getVariable(function);
            String changed = addSplitters(function, variable, VARIABLE_SPLITTER);

            return changed.replace(VARIABLE_SPLITTER + variable, MathParser.Precision.evadeEFormat(value));
        }

        public static final String replaceConstants(final String function) {
            String changed = function;
            for (HashMap.Entry<String, Double> entry : MATH_CONSTANTS.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();

                changed = changed.replaceAll(key, MathParser.Precision.evadeEFormat(value));
            }

            return changed;
        }

        public static final ArrayList<String> getCoefficients(final String function) {
            String variable = getVariable(function);
            String changed = replaceConstants(deleteDeclaration(function));
            HashSet<String> coefficients = new HashSet<>();

            changed = deleteFunctions(changed);

            StringBuilder insertable = new StringBuilder(changed);

            for (int i = 0; i < insertable.length(); i++) {
                String current = singleAt(insertable, i);

                if (isLetter(current) && !current.equals(variable))
                    coefficients.add(current);
            }

            return new ArrayList<>(coefficients);
        }

        public static final String replaceCoefficient(final String function, final String coefficient, final double value) {
            StringBuilder changed = new StringBuilder(replaceConstants(deleteDeclaration(function)));

            int len = changed.length();

            for (int i = 0; i < len; i++) {
                String current = singleAt(changed, i);
                String left =
                        i != 0 ? singleAt(changed, i - 1) : " ";
                String right =
                        i != len - 1 ? singleAt(changed, i + 1) : " ";

                if (current.equals(coefficient) && isNotLetter(left) && isNotLetter(right)) {
                    changed.insert(i, COEFFICIENT_SPLITTER);
                }
            }

            return changed.toString().replace(COEFFICIENT_SPLITTER + coefficient, MathParser.Precision.evadeEFormat(value));
        }

        @Deprecated
        public static HashMap<String, List<Integer>> getCoeffs(String expression, String variable) {
            String _s = deleteDeclaration(expression);

            String _pre_string = deleteDeclaration(expression);
            _pre_string = replaceConstants(_pre_string);
            StringBuilder _expression = new StringBuilder(expression);
            HashMap<String, List<Integer>> _coeffs = new HashMap<>();

            for (String name: FUNCTION_MAP.keySet()) {
                _expression = new StringBuilder(_expression.toString().replace(name, ""));
            }

            _expression = new StringBuilder(_expression.toString().replace("arc", ""));

            _expression = new StringBuilder(_expression.toString().replace("E", ""));

            String[] _symbols = new String[] {"\\+", "\\-",
                    "\\*", "\\/",
                    "\\(", "\\)",
                    "\\,", "\\."};

            List<String> symbols = new ArrayList<>(java.util.Arrays.asList(_symbols));

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

            for (String name: FUNCTION_MAP.keySet()) {
                _expression = new StringBuilder(_expression.toString().replace(name, ""));
            }

            _expression = new StringBuilder(_expression.toString().replace("arc", ""));

            _expression = new StringBuilder(_expression.toString().replace("E", ""));

            String[] _symbols = new String[] {"\\+", "\\-",
                    "\\*", "\\/",
                    "\\(", "\\)",
                    "\\,", "\\."};

            List<String> symbols = new ArrayList<>(java.util.Arrays.asList(_symbols));

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
            equation = replaceConstants(equation);
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
            return _split.toString().replace(COEFFICIENT_SPLITTER + coeff, java.lang.Double.toString(value));
        }
    }
}
