package com.mathp;

import java.text.DecimalFormat;
import java.util.*;

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

    private static final HashMap<String, MathFunction> FUNCTION_MAP = getFunctionMap();

    public static double parse(final String expression) {

        List<Lexeme> lexemeList = null;
        try {
            lexemeList = MathParser.lexParse(FunctionHandler.replaceConstants(expression));
        } catch (SyntaxParseException e) {
            e.printStackTrace();
        }

        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemeList);

        try {
            return Syntax.EXPRESSION(lexemeBuffer);
        } catch (SyntaxParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double compute(final String function, final double value, final HashMap<String, Double> map) {
        return 0;
    }

    public static double[][] threadedGetData() {
        return new double[][]{};
    }

    public static HashMap<String, MathFunction> getFunctionMap() {
        HashMap<String, MathFunction> functionMap = new HashMap<>();
        functionMap.put("abs", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function abs(arg)");
            else {
                return Math.abs(args.get(0));
            }
        });

        functionMap.put("pow", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function pow(arg, arg)");
            else {
                return Math.pow(args.get(0), args.get(1));
            }
        });

        functionMap.put("sin", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sin(arg)");
            else {
                return Math.sin(args.get(0));
            }
        });

        functionMap.put("cos", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cos(arg)");
            else {
                return Math.cos(args.get(0));
            }
        });

        functionMap.put("tg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted at least 1 argument in function sqrt(arg)");
            else {
                return Math.tan(args.get(0));
            }
        });

        functionMap.put("ctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function ctg(arg)");
            else {
                return 1.0 / Math.tan(args.get(0));
            }
        });

        functionMap.put("log", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function log(arg, arg)");
            else {
                return Math.log(args.get(1)) / Math.log(args.get(0));
            }
        });

        functionMap.put("ln", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function ln(arg)");
            else {
                return Math.log(args.get(0));
            }
        });

        functionMap.put("cbrt", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cbrt(arg)");
            else {
                return Math.cbrt(args.get(0));
            }
        });

        functionMap.put("sqrt", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sqrt(arg)");
            else {
                return Math.sqrt(args.get(0));
            }
        });

        functionMap.put("min", args -> {
            if (args.isEmpty())
                throw new IllegalArgumentException("Found 0 arguments, excepted at least 1 argument in function min(args...)");
            if (args.size() == 1)
                return args.get(0);
            double min = args.get(0);
            for (Double value: args)
                if (value < min)
                    min = value;
            return min;
        });

        functionMap.put("max", args -> {
            if (args.isEmpty())
                throw new IllegalArgumentException("Found 0 arguments, excepted at least 1 argument in function max(args...)");
            if (args.size() == 1)
                return args.get(0);
            double max = args.get(0);
            for (Double value: args)
                if (value > max)
                    max = value;
            return max;
        });

        functionMap.put("sq", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sq(arg)");
            return args.get(0) * args.get(0);
        });

        functionMap.put("cb", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cb(arg)");
            return args.get(0) * args.get(0) * args.get(0);
        });

        functionMap.put("arcsin", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arcsin(arg)");
            return java.lang.Math.asin(args.get(0));
        });

        functionMap.put("arccos", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arccos(arg)");
            return java.lang.Math.acos(args.get(0));
        });

        functionMap.put("arctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arctg(arg)");
            return java.lang.Math.atan(args.get(0));
        });

        functionMap.put("arctg2", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function arctg2(arg1, arg2)");
            return java.lang.Math.atan2(args.get(0), args.get(1));
        });

        functionMap.put("arcctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arcctg(arg)");
            double arg = args.get(0);
            return java.lang.Math.acos(arg / java.lang.Math.sqrt(1 + arg * arg));
        });

        functionMap.put("rand", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function rand(min, max)");
            double left = args.get(0);
            double right = args.get(1);
            return (Math.random() * (right - left) + 1) + left;
        });

        return functionMap;
    }

    public static class Lexeme {
        public LexemeType type;
        public String string;

        public Lexeme(LexemeType _ctype, String _cstring) {
            this.type = _ctype;
            this.string = _cstring;
        }

        public Lexeme(LexemeType _ctype, Character _char) {
            this.type = _ctype;
            this.string = _char.toString();
        }

        public LexemeType getType() {
            return this.type;
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", string='" + string + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer {
        private int pos;
        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> _lexemes) {
            this.lexemes = _lexemes;
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

    public static List<Lexeme> lexParse(String _expression) throws SyntaxParseException {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < _expression.length()) {
            char symbol = _expression.charAt(pos);
            switch (symbol) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT, symbol));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT, symbol));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OPERATOR_ADD, symbol));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OPERATOR_SUB, symbol));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OPERATOR_MUL, symbol));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OPERATOR_DIV, symbol));
                    pos++;
                    continue;
                case ',':
                    lexemes.add(new Lexeme(LexemeType.COMMA, symbol));
                    pos++;
                    continue;
                default:
                    if ((symbol <= '9' && symbol >= '0') | (symbol == '.')) {

                        StringBuilder number = new StringBuilder();
                        do {
                            number.append(symbol);
                            pos++;
                            if (pos >= _expression.length())
                                break;
                            symbol = _expression.charAt(pos);
                        } while ((symbol <= '9' && symbol >= '0') | (symbol == '.'));
                        lexemes.add(new Lexeme(LexemeType.NUMBER, number.toString()));

                    } else {
                        if (symbol != ' ') {
                            if ((symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z')) {

                                StringBuilder function = new StringBuilder();
                                do {
                                    function.append(symbol);
                                    pos++;
                                    if (pos >= _expression.length())
                                        break;
                                    symbol = _expression.charAt(pos);
                                } while ((symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z'));
                                if (FUNCTION_MAP.containsKey(function.toString())) {
                                    lexemes.add(new Lexeme(LexemeType.FUNCTION_NAME, function.toString()));
                                } else {
                                    throw new RuntimeException();
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
        lexemes.add(new Lexeme(LexemeType.EOF, ""));

        return lexemes;
    }

    public static final class Precision {

        private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

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

        public static final String _fformat(double value)
        {
            return new DecimalFormat("0.0000000000").format(value).replace(",",".");
        }
    }

    public static final class Syntax {
        public static double EXPRESSION(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            Lexeme lexeme = lexemeBuffer.next();
            if (lexeme.type == LexemeType.EOF) {
                return 0;
            } else {
                lexemeBuffer.back();
                return ADD_SUBTRACT(lexemeBuffer);
            }
        }

        public static double ADD_SUBTRACT(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            double value = MULTIPLY_DIVIDE(lexemeBuffer);
            while (true) {
                Lexeme lexeme = lexemeBuffer.next();
                switch (lexeme.type) {
                    case OPERATOR_ADD:
                        value += MULTIPLY_DIVIDE(lexemeBuffer);
                        break;
                    case OPERATOR_SUB:
                        value -= MULTIPLY_DIVIDE(lexemeBuffer);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                        lexemeBuffer.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                }
            }
        }

        public static double MULTIPLY_DIVIDE(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            double value = FACTOR(lexemeBuffer);
            while (true) {
                Lexeme lexeme = lexemeBuffer.next();
                switch (lexeme.type) {
                    case OPERATOR_MUL:
                        value *= FACTOR(lexemeBuffer);
                        break;
                    case OPERATOR_DIV:
                        value /= FACTOR(lexemeBuffer);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                    case OPERATOR_ADD:
                    case OPERATOR_SUB:
                        lexemeBuffer.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                }
            }
        }

        public static double FACTOR(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            Lexeme lexeme = lexemeBuffer.next();
            switch (lexeme.type) {
                case FUNCTION_NAME:
                    lexemeBuffer.back();
                    return FUNCTION(lexemeBuffer);
                case OPERATOR_SUB:
                    double positive_value = FACTOR(lexemeBuffer);
                    return -positive_value;
                case NUMBER:
                    try {
                        return Double.parseDouble(lexeme.string);
                    } catch (NumberFormatException e) {
                        e.getMessage();
                    }
                case LEFT:
                    double value = EXPRESSION(lexemeBuffer);
                    lexeme = lexemeBuffer.next();
                    if (lexeme.type != LexemeType.RIGHT) {
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
                    }
                    return value;
                default:
                    throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + lexemeBuffer.getPos() + " in expression");
            }
        }

        public static double FUNCTION(final LexemeBuffer lexemeBuffer) throws SyntaxParseException {
            String function = lexemeBuffer.next().string;
            Lexeme lexeme = lexemeBuffer.next();

            if (lexeme.type != LexemeType.LEFT) {
                throw new SyntaxParseException();
            }

            ArrayList<Double> args = new ArrayList<>();
            lexeme = lexemeBuffer.next();

            if (lexeme.type != LexemeType.RIGHT) {
                lexemeBuffer.back();
                do {

                    args.add(EXPRESSION(lexemeBuffer));
                    lexeme = lexemeBuffer.next();

                    if ((lexeme.type != LexemeType.COMMA) && (lexeme.type != LexemeType.RIGHT)) {
                        throw new SyntaxParseException();
                    }

                } while (lexeme.type == LexemeType.COMMA);
            }
            return FUNCTION_MAP.get(function).apply(args);
        }
    }

    public static final class FunctionHandler {

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
                e.printStackTrace();
            }
            return new HashMap<>();
        }

        private static boolean equalsAnyOf(final String string, final HashMap<String, Double> map) {
            int size = map.size();
            for (int i = 0; i < size; i++) {
                if (string.equals(map.get(i)))
                    return true;
            }
            return false;
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

            return changed.replace(VARIABLE_SPLITTER + variable, MathParser.Precision._fformat(value));
        }

        public static final String replaceConstants(final String function) {
            String changed = function;
            for (HashMap.Entry<String, Double> entry : MATH_CONSTANTS.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();

                changed = changed.replaceAll(key, MathParser.Precision._fformat(value));
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

            return changed.toString().replace(COEFFICIENT_SPLITTER + coefficient, String.valueOf(value));
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
