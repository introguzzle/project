package com.mathp;

import java.text.DecimalFormat;
import java.util.*;

public class MathParser {
    public static class SyntaxParseException extends Exception {
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

    public enum LexemeType {
        OPERATOR_ADD, OPERATOR_SUB, OPERATOR_MUL, OPERATOR_DIV,
        LEFT, RIGHT,
        NUMBER, COMMA, NAME,
        EOF
    }

    public interface Function {
        double apply(List<Double> args);
    }

    public static HashMap<String, Function> getFunctionMap() {
        HashMap<String, Function> functionMap = new HashMap<>();
        functionMap.put("abs", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.abs(args.get(0));
            }
        });

        functionMap.put("pow", args -> {
            if (args.size() != 2)
                throw new RuntimeException();
            else {
                return java.lang.Math.pow(args.get(0), args.get(1));
            }
        });

        functionMap.put("sin", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.sin(args.get(0));
            }
        });

        functionMap.put("cos", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.cos(args.get(0));
            }
        });

        functionMap.put("tg", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.tan(args.get(0));
            }
        });

        functionMap.put("ctg", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return (double)1.0 / java.lang.Math.tan(args.get(0));
            }
        });

        functionMap.put("log", args -> {
            if (args.size() != 2)
                throw new RuntimeException();
            else {
                return java.lang.Math.log(args.get(1)) / java.lang.Math.log(args.get(0));
            }
        });

        functionMap.put("ln", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.log(args.get(0));
            }
        });

        functionMap.put("cbrt", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.cbrt(args.get(0));
            }
        });

        functionMap.put("sqrt", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            else {
                return java.lang.Math.sqrt(args.get(0));
            }
        });

        functionMap.put("min", args -> {
            if (args.isEmpty())
                throw new RuntimeException();
            double min = args.get(0);
            for (Double value: args)
                if (value < min)
                    min = value;
            return min;
        });

        functionMap.put("max", args -> {
            if (args.isEmpty())
                throw new RuntimeException();
            double max = args.get(0);
            for (Double value: args)
                if (value > max)
                    max = value;
            return max;
        });

        functionMap.put("sq", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            return args.get(0) * args.get(0);
        });

        functionMap.put("cb", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            return args.get(0) * args.get(0) * args.get(0);
        });

        functionMap.put("arcsin", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            return java.lang.Math.asin(args.get(0));
        });

        functionMap.put("arccos", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            return java.lang.Math.acos(args.get(0));
        });

        functionMap.put("arctg", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            return java.lang.Math.atan(args.get(0));
        });

        functionMap.put("arctg2", args -> {
            if (args.size() != 2)
                throw new RuntimeException();
            return java.lang.Math.atan2(args.get(0), args.get(1));
        });

        functionMap.put("arcctg", args -> {
            if (args.size() != 1)
                throw new RuntimeException();
            double arg = args.get(0);
            return java.lang.Math.acos(arg / java.lang.Math.sqrt(1 + arg * arg));
        });

        functionMap.put("rand", args -> {
            if (args.size() != 2)
                throw new RuntimeException();
            return (Math.random() * args.get(0) - args.get(0) + 1.0) - args.get(1);
        });

        return functionMap;
    }

    public static HashMap<String, Function> _functionMap = getFunctionMap();

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
                                if (_functionMap.containsKey(function.toString())) {
                                    lexemes.add(new Lexeme(LexemeType.NAME, function.toString()));
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

    public static class Precision {
        private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

        public static String _format(double value, int precision) {
            StringBuilder _string = new StringBuilder();
            if (value < 0) {
                _string.append('-');
                value = -value;
            }
            int exp = POW10[precision];
            long lvalue = (long)(value * exp + 0.5);
            _string.append(lvalue / exp).append('.');
            long fvalue = lvalue % exp;
            for (int p = precision - 1; p > 0 && fvalue < POW10[p]; p--) {
                _string.append('0');
            }
            _string.append(fvalue);
            return _string.toString();
        }

        public static String _fformat(double value)
        {
            DecimalFormat formatter;

            if (value - (int)value > 0.0)
                formatter = new DecimalFormat("0.0000000000");
            else
                formatter = new DecimalFormat("0.0000000000");

            return formatter.format(value).replace(",",".");
        }
    }

    public static class Syntax {
        public static double EXPRESSION(LexemeBuffer _lexemes) throws SyntaxParseException {
            Lexeme lexeme = _lexemes.next();
            if (lexeme.type == LexemeType.EOF) {
                return 0;
            } else {
                _lexemes.back();
                return ADD_SUBTRACT(_lexemes);
            }
        }

        public static double ADD_SUBTRACT(LexemeBuffer _lexemes) throws SyntaxParseException {
            double value = MULTIPLY_DIVIDE(_lexemes);
            while (true) {
                Lexeme lexeme = _lexemes.next();
                switch (lexeme.type) {
                    case OPERATOR_ADD:
                        value += MULTIPLY_DIVIDE(_lexemes);
                        break;
                    case OPERATOR_SUB:
                        value -= MULTIPLY_DIVIDE(_lexemes);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                        _lexemes.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + _lexemes.getPos() + " in expression");
                }
            }
        }

        public static double MULTIPLY_DIVIDE(LexemeBuffer _lexemes) throws SyntaxParseException {
            double value = FACTOR(_lexemes);
            while (true) {
                Lexeme lexeme = _lexemes.next();
                switch (lexeme.type) {
                    case OPERATOR_MUL:
                        value *= FACTOR(_lexemes);
                        break;
                    case OPERATOR_DIV:
                        value /= FACTOR(_lexemes);
                        break;
                    case EOF:
                    case RIGHT:
                    case COMMA:
                    case OPERATOR_ADD:
                    case OPERATOR_SUB:
                        _lexemes.back();
                        return value;
                    default:
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + _lexemes.getPos() + " in expression");
                }
            }
        }

        public static double FACTOR(LexemeBuffer _lexemes) throws SyntaxParseException {
            Lexeme lexeme = _lexemes.next();
            switch (lexeme.type) {
                case NAME:
                    _lexemes.back();
                    return FUNCTION(_lexemes);
                case OPERATOR_SUB:
                    double positive_value = FACTOR(_lexemes);
                    return -positive_value;
                case NUMBER:
                    try {
                        return Double.parseDouble(lexeme.string);
                    } catch (NumberFormatException e) {
                        e.getMessage();
                    }
                case LEFT:
                    double value = EXPRESSION(_lexemes);
                    lexeme = _lexemes.next();
                    if (lexeme.type != LexemeType.RIGHT) {
                        throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + _lexemes.getPos() + " in expression");
                    }
                    return value;
                default:
                    throw new SyntaxParseException("Unexpected token: '" + lexeme.getType() + "' at pos " + _lexemes.getPos() + " in expression");
            }
        }

        public static double FUNCTION(LexemeBuffer _lexemes) throws SyntaxParseException {
            String function = _lexemes.next().string;
            Lexeme lexeme = _lexemes.next();

            if (lexeme.type != LexemeType.LEFT) {
                throw new SyntaxParseException();
            }

            ArrayList<Double> args = new ArrayList<>();
            lexeme = _lexemes.next();

            if (lexeme.type != LexemeType.RIGHT) {
                _lexemes.back();
                do {

                    args.add(EXPRESSION(_lexemes));
                    lexeme = _lexemes.next();

                    if ((lexeme.type != LexemeType.COMMA) && (lexeme.type != LexemeType.RIGHT)) {
                        throw new SyntaxParseException();
                    }

                } while (lexeme.type == LexemeType.COMMA);
            }
            return _functionMap.get(function).apply(args);
        }
    }

    public static class FunctionHandle {

        private static final String FUNCTION_DECLARATION = "f(variable) = ";

        private static final String MATH_CONSTANTS_PREFIX = "mp_";
        private static final List<String> MATH_CONSTANTS_NAMES = Arrays.asList(MATH_CONSTANTS_PREFIX + "pi",
                MATH_CONSTANTS_PREFIX + "e",
                MATH_CONSTANTS_PREFIX + "phi",
                MATH_CONSTANTS_PREFIX + "m"
        );
        private static final List<Double> MATH_CONSTANTS_VALUES = Arrays.asList(3.14592, 2.71828, 1.61803, 0.57721);
        private static final String VARIABLE_SPLITTER = "V";
        private static final String COEFFICIENT_SPLITTER = "C";

        // phi is the golden ratio
        // m is the euler-mascheroni constant

//        private static final HashMap<String, Double> MATH_CONSTANTS = new HashMap<>() {{
//            MATH_CONSTANTS.put(_PI, _PI_VALUE);
//
//        }};

        public static final HashMap<String, Double> MATH_CONSTANTS = zip(MATH_CONSTANTS_NAMES, MATH_CONSTANTS_VALUES);

        private static HashMap<String, Double> zip(List<String> a, List<Double> b) {
            try {
                HashMap<String, Double> map = new HashMap<>();
                for (int i = 0; i < a.size(); i++) {
                    map.put(a.get(i), b.get(i));
                }
                return map;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return new HashMap<>();
        }

        private static boolean equalsAnyOf(String string, HashMap<String, Double> map) {
            int size = map.size();
            for (int i = 0; i < size; i++) {
                if (string.equals(map.get(i)))
                    return true;
            }
            return false;
        }

        public static boolean isFunction(String expression) {
            return (expression.contains(FUNCTION_DECLARATION.substring(0, 2)));
        }

        public static String deleteDeclaration(String expression) {
            int equals = expression.indexOf("=");
            StringBuilder _expr = new StringBuilder(new StringBuilder(expression).substring(equals + 1));
            int index = 0;
            return _expr.toString().replace(" ", "");
        }

        public static String getVariable(String expression) {
            int equalsSignIndex = expression.indexOf("=");
            if (equalsSignIndex == -1)
                return "-1";

            String declaration = expression.substring(0, equalsSignIndex);

            int leftBracketSignIndex = declaration.indexOf("(");
            int rightBracketSignIndex = declaration.indexOf(")");
            if (leftBracketSignIndex == -1 || rightBracketSignIndex == -1)
                return "-1";

            String inner = declaration.substring(leftBracketSignIndex + 1, rightBracketSignIndex);

//            System.out.println(inner);
//            int variablePosition = inner.indexOf("[a-zA-Z]");
//            if (variablePosition == -1)
//                return "-1";
            String regex = "[a-zA-Z]";

            String after = expression.substring(equalsSignIndex);
            String trueVariable = "-1";

            for (int i = 0; i < after.length(); i++) {
                char left = (i != 0) ? after.charAt(i - 1) : ' ';
                char right = (i != after.length() - 1) ? after.charAt(i + 1) : ' ';
                if ((Character.toString(after.charAt(i)).equals(inner)) & (!Character.isLetter(left)) & (!Character.isLetter(right)))
                    trueVariable = Character.toString(after.charAt(i));
            }

            return trueVariable;
        }

        public static String replaceVariable(String expression, double value) {
            if (!(isFunction(expression))) {
                return expression;
            }

            String variable = getVariable(expression);

            String equation = deleteDeclaration(expression);
            StringBuilder _split = new StringBuilder(equation);
            int entry = 0;


            for (int i = 0; i < equation.length(); i++) {
                String current = Character.toString(_split.charAt(i));
                char left = (i != 0) ? _split.charAt(i - 1) : ' ';
                char right = (i != equation.length() - 1) ? _split.charAt(i + 1) : ' ';

                if ((current.equals(variable)) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
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

                if ((current.equals(variable)) & (!Character.isLetter(left)) & (!Character.isLetter(right))) {
                    _split.insert(i, VARIABLE_SPLITTER);
                }
            }

            return _split.toString().replace(VARIABLE_SPLITTER + variable, MathParser.Precision._fformat(value));
        }

        public static String replaceConstants(String expression) {
            String equation = expression;
            for (HashMap.Entry<String, Double> entry : MATH_CONSTANTS.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();

                equation = equation.replaceAll(key, MathParser.Precision._fformat(value));
            }

            return equation;
        }

        public static HashMap<String, List<Integer>> getCoeffs(String expression, String variable) {
            String _s = deleteDeclaration(expression);

            String _pre_string = deleteDeclaration(expression);
            _pre_string = replaceConstants(_pre_string);
            StringBuilder _expression = new StringBuilder(expression);
            HashMap<String, List<Integer>> _coeffs = new HashMap<>();

            for (String name: _functionMap.keySet()) {
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

//            for (int i = 0; i < _expression.length(); i++) {
//                if (!(Character.isDigit(_expression.charAt(i))) & ((Character.isLetter(_expression.charAt(i))))) {
//                    List<Integer> entries = new ArrayList<>();
//                    for (int _i = 0; _i < this.expression.length(); _i++) {
//                        if (this.expression.charAt(_i) == _expression.charAt(i)) {
//                            entries.add(_i);
//                            // System.out.println(java.util.Arrays.toString(occurrences.toArray()) + "\n");
//                        }
//                    }
//                    _coeffs.put(Character.toString(_expression.charAt(i)), entries);
//                }
//            }
            return _coeffs;
        }

        public static HashMap<String, List<Integer>> getCoeffs(String expression) {
            String variable = getVariable(expression);
            boolean containsVariable = true;

            if (getVariable(expression).equals("-1")) {
                containsVariable = false;
            }


            String _s = deleteDeclaration(expression);
            StringBuilder _expression = new StringBuilder(deleteDeclaration(expression));
            HashMap<String, List<Integer>> _coeffs = new HashMap<>();

            for (String name: _functionMap.keySet()) {
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
