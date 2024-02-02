package ru.grapher;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;

public final class MathParser implements MathConstants {

    private static final DecimalFormat NUMBER_FORMAT    =
            new DecimalFormat("#.#############################################");


    private static final ArrayList<String> SORTED_NAMES = new ArrayList<>();

    static {
        SORTED_NAMES.addAll(MATH_CONSTANTS.keySet());

        SORTED_NAMES.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
    }

    private MathParser() throws InstantiationException {
        throw new InstantiationException();
    }

    public static String replaceConstants(final String function) {

        StringBuilder changed = new StringBuilder(function);

        boolean exist = false;

        for (var name: SORTED_NAMES) {
            exist |= changed.toString().matches(".*\\b" + name + "\\b.*");
        }

        if (!exist)
            return function;

        for (var name : SORTED_NAMES) {
            String value = Precision.evadeEFormat(MATH_CONSTANTS.get(name));

            int i = 0;
            int index = 0;

            if (changed.toString().matches(".*\\b" + name + "\\b.*")) {
                while (changed.indexOf(name, i) != -1 && i < changed.length()) {

                    char prev = index != 0
                            ? changed.charAt(index - 1)
                            : ' ';

                    char next = index + name.length() < changed.length()
                            ? changed.charAt(index + name.length())
                            : ' ';

                    if (!Character.isLetter(prev) && !Character.isLetter(next)) {
                        index = changed.indexOf(name, i);
                        changed.insert(index, PREFIX);
                        i = index + name.length() + 1;
                    } else {
                        index = changed.indexOf(name, i);
                        i++;
                    }
                }

                changed = new StringBuilder(changed.toString().replace(PREFIX + name, value));
            }
        }

        return changed.toString();
    }

    public static boolean isParsable(final String expression) {
        return getParsingResult(expression) != ParsingResult.ERROR;
    }

    public static ParsingResult getParsingResult(final String expression) {
        if (expression.isEmpty()) {
            return ParsingResult.ERROR;
        }

        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(expression)) {
            return ParsingResult.ERROR;
        }

        Exception ex = null;
        String changed = replaceConstants(expression);

        try {
            parseNoHandling(changed);
        } catch (Exception e) {
            ex = e;
        }

        if (ex == null)
            return ParsingResult.EXPRESSION;

        try {
            FunctionParsingUtilities.isDeclaredImplicit(changed);
        } catch (StringIndexOutOfBoundsException e) {
            return ParsingResult.ERROR;
        }

        Exception last = null;
        String pf = changed;

        try {
            var coefficients = FunctionParsingUtilities.Parametric.getCoefficients(changed);
            for (var c: coefficients) {
                pf = FunctionParsingUtilities.Parametric.replaceCoefficient(pf, c, 1.0);
            }
            parseNoHandling(pf);
        } catch (Exception e) {
            last = e;
        }

        if (last == null && !FunctionParsingUtilities.Explicit.isFunction(pf))
            return ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS;

        if (FunctionParsingUtilities.Parametric.isFunction(changed)) {
            final double t = 1.0;

            String pchanged = changed;
            Exception exception = null;

            try {
                parseNoHandling(pchanged);
            } catch (Exception e) {
                exception = e;
            }

            if (exception == null)
                return ParsingResult.EXPRESSION;

            exception = null;

            try {
                pchanged = FunctionParsingUtilities.Parametric.replaceVariable(pchanged, t);
                parseNoHandling(pchanged);
            } catch (Exception e) {

                exception = e;
                Exception err = null;

                try {
                    for (var s: FunctionParsingUtilities.Parametric.getCoefficients(pchanged))
                        pchanged = FunctionParsingUtilities.Parametric.replaceCoefficient(pchanged, s, t);

                    parseNoHandling(pchanged);
                } catch (Exception error) {
                    err = error;
                }

                if (err == null)
                    return ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS;
            }

            if (exception == null)
                return ParsingResult.PARAMETRIC_FUNCTION;
        }

        if (!FunctionParsingUtilities.isDeclaredImplicit(changed)) {
            final double x = 1.0;

            try {
                parseNoHandling(expression);
            } catch (IndexOutOfBoundsException e) {
                return ParsingResult.ERROR;
            } catch (MathParserException notExpression) {

                try {
                    changed = FunctionParsingUtilities.Explicit.replaceVariable(changed, x);

                    if (changed.isEmpty()) {
                        return ParsingResult.ERROR;
                    }

                    double ignored = parseNoHandling(changed);
                } catch (MathParserException notFunction) {

                    try {

                        for (var coefficient : FunctionParsingUtilities.Explicit.getCoefficients(changed)) {
                            changed = FunctionParsingUtilities.Explicit.replaceCoefficient(changed, coefficient, x);
                        }

                        double ignored = parseNoHandling(changed);

                    } catch (IllegalArgumentException e) {
                        return ParsingResult.ERROR;

                    } catch (IndexOutOfBoundsException out) {
                        return ParsingResult.ERROR;

                    } catch (MathParserException notAnything) {
                        return ParsingResult.ERROR;
                    }

                    return ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS;
                } catch (IllegalArgumentException ill) {
                    return ParsingResult.ERROR;

                } catch (IndexOutOfBoundsException out) {
                    return ParsingResult.ERROR;
                }

                return ParsingResult.EXPLICIT_FUNCTION;
            }

        } else {

        }

        Exception exception = null;

        try {
            MathParser.parseNoHandling(changed);
        } catch (MathParserException | IllegalArgumentException | IndexOutOfBoundsException e) {
            exception = e;
        }

        if (exception == null)
            return ParsingResult.EXPRESSION;
        else
            return ParsingResult.ERROR;
    }

    public static double parseNoHandling(final String expression) throws MathParserException {
        return Syntax.expression(new TokenBuffer(tokenize(replaceConstants(expression))));
    }

    public static double parse(final String expression) {

        List<Token> tokenList = new ArrayList<>();
        try {
            tokenList = tokenize(replaceConstants(expression));
        } catch (MathParserException e) {

        }

        TokenBuffer tokenBuffer = new TokenBuffer(tokenList);

        try {
            return Syntax.expression(tokenBuffer);
        } catch (MathParserException e) {

        }

        return 0;
    }

    public static double parseWithLogging(final String expression) {

        List<Token> tokenList = null;
        try {
            tokenList = tokenize(replaceConstants(expression));
        } catch (MathParserException e) {
            Grapher.getLogger().log(Level.SEVERE, "Wrong expression", e);
        }

        TokenBuffer tokenBuffer = new TokenBuffer(tokenList);

        try {
            return Syntax.expression(tokenBuffer);
        } catch (MathParserException e) {
            Grapher.getLogger().log(Level.SEVERE, "Wrong expression", e);
        }

        return 0;
    }

    public static List<Token> tokenize(String expression) throws MathParserException {
        ArrayList<Token> tokenList = new ArrayList<>();

        Stack<Character> stack = new Stack<>();

        int pos = 0;

        while (pos < expression.length()) {

            char character = expression.charAt(pos);
            switch (character) {
                case '(':
                    tokenList.add(new Token(TokenType.LEFT_BRACKET, character));
                    pos++;
                    stack.push(character);
                    continue;
                case ')':
                    tokenList.add(new Token(TokenType.RIGHT_BRACKET, character));
                    pos++;
                    if (stack.isEmpty())
                        throw new MathParserException("Illegal brackets");
                    else
                        stack.pop();
                    continue;
                case '+':
                    tokenList.add(new Token(TokenType.OPERATOR_ADD, character));
                    pos++;
                    continue;
                case '-':
                    tokenList.add(new Token(TokenType.OPERATOR_SUB, character));
                    pos++;
                    continue;
                case '*':
                    if (expression.charAt(pos + 1) == '*') {
                        tokenList.add(new Token(
                                TokenType.OPERATOR_EXP,
                                String.valueOf(character) + String.valueOf(expression.charAt(pos + 1)))
                        );
                        pos += 2;
                        continue;
                    } else {
                        tokenList.add(new Token(TokenType.OPERATOR_MUL, character));
                        pos++;
                        continue;
                    }
                case '/':
                    tokenList.add(new Token(TokenType.OPERATOR_DIV, character));
                    pos++;
                    continue;
                case '^':
                    tokenList.add(new Token(TokenType.OPERATOR_EXP, character));
                    pos++;
                    continue;
                case ',':
                    tokenList.add(new Token(TokenType.COMMA, character));
                    pos++;
                    continue;
                default:
                    if ((character <= '9' && character >= '0') || (character == '.')) {

                        StringBuilder number = new StringBuilder();
                        do {
                            number.append(character);
                            pos++;
                            if (pos >= expression.length())
                                break;
                            character = expression.charAt(pos);
                        } while ((character <= '9' && character >= '0') || (character == '.'));
                        tokenList.add(new Token(TokenType.NUMBER, number.toString()));

                    } else {
                        if (character != ' ') {
                            if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')) {

                                StringBuilder function = new StringBuilder();

                                do {
                                    function.append(character);
                                    pos++;
                                    if (pos >= expression.length())
                                        break;
                                    character = expression.charAt(pos);
                                } while ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z'));

                                if (MathFunctions.FUNCTION_MAP.containsKey(function.toString())) {
                                    tokenList.add(new Token(TokenType.FUNCTION_NAME, function.toString()));

                                } else {
                                    throw new MathParserException("Unexpected function: " + function);
                                }

                            } else {
                                throw new MathParserException("Unexpected character: '" + character + "' at pos " + pos + " in expression");
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }

        if (!stack.isEmpty()) {
            throw new MathParserException("Bracket count don't match");
        }

        tokenList.add(new Token(TokenType.EOF, ""));

        return tokenList;
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
//    EXPONENT :  FACTOR ( '^' FACTOR )* ;
//
//    FACTOR : FUNCTION | UNARY | NUMBER | '(' EXPRESSION ')' ;
//
//    UNARY: '-' FACTOR
//
//    FUNCTION: NAME '(' EXPRESSION(, EXPRESSION)+)? ')'
//
//

    public static class Token {
        public TokenType tokenType;
        public String data;

        public Token(TokenType tokenType, String data) {
            this.tokenType = tokenType;
            this.data = data;
        }

        public Token(TokenType tokenType, Character data) {
            this.tokenType = tokenType;
            this.data = data.toString();
        }

        public TokenType getTokenType() {
            return this.tokenType;
        }

        public String getData() {
            return this.data;
        }

        @Override
        public String toString() {
            return "Token{" + "type=" + tokenType + ", data= '" + data + "'}";
        }
    }

    public static class TokenBuffer {
        private int pos;
        public List<Token> tokens;

        public TokenBuffer(List<Token> tokens) {
            this.tokens = tokens;
        }

        public Token getNextToken() {
            return tokens.get(pos++);
        }

        public void returnBack() {
            pos--;
        }

        public int getPos() {
            return pos;
        }

    }

    public static final class Precision {

        private Precision() throws ClassNotFoundException {
            throw new ClassNotFoundException();
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
            long lvalue = (long)(v * exp + 0.5);

            result.append(lvalue / exp).append('.');

            long fvalue = lvalue % exp;

            for (int p = precision - 1; p > 0 && fvalue < POW10[p]; p--) {
                result.append('0');
            }

            result.append(fvalue);

            return result.toString();
        }

        public static String evadeEFormat(double value) {
            return NUMBER_FORMAT.format(value).replace(",",".");
        }
    }

    public static final class Syntax {

        private Syntax() throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        public static double expression(final TokenBuffer tokenBuffer) throws MathParserException {
            Token token = tokenBuffer.getNextToken();
            if (token.tokenType == TokenType.EOF) {
                return 0;
            } else {
                tokenBuffer.returnBack();
                return addSubtract(tokenBuffer);
            }
        }

        public static double addSubtract(final TokenBuffer tokenBuffer) throws MathParserException {
            double value = multiplyDivide(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.tokenType) {
                    case OPERATOR_ADD:
                        value += multiplyDivide(tokenBuffer);
                        break;
                    case OPERATOR_SUB:
                        value -= multiplyDivide(tokenBuffer);
                        break;
                    case EOF:
                    case RIGHT_BRACKET:
                    case COMMA:
                        tokenBuffer.returnBack();
                        return value;
                    default:
                        throw new MathParserException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                }
            }
        }

        public static double multiplyDivide(final TokenBuffer tokenBuffer) throws MathParserException {
            double value = exp(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.tokenType) {
                    case OPERATOR_MUL:
                        value *= exp(tokenBuffer);
                        break;
                    case OPERATOR_DIV:
                        value /= exp(tokenBuffer);
                        break;
                    case EOF:
                    case RIGHT_BRACKET:
                    case COMMA:
                    case OPERATOR_ADD:
                    case OPERATOR_SUB:
                        tokenBuffer.returnBack();
                        return value;
                    default:
                        throw new MathParserException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                }
            }
        }

        public static double exp(final TokenBuffer tokenBuffer) throws MathParserException {
            double value = factor(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.tokenType) {
                    case OPERATOR_EXP:
                        value = Math.pow(value, exp(tokenBuffer));
                        break;

                    case OPERATOR_MUL:
                        tokenBuffer.returnBack();
                        return value;

                    case OPERATOR_DIV:
                        tokenBuffer.returnBack();
                        return value;

                    case EOF:
                    case RIGHT_BRACKET:
                    case COMMA:
                    case OPERATOR_ADD:
                    case OPERATOR_SUB:
                        tokenBuffer.returnBack();
                        return value;

                    default:
                        throw new MathParserException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                }
            }
        }

        public static double factor(final TokenBuffer tokenBuffer) throws MathParserException {
            Token token = tokenBuffer.getNextToken();

            switch (token.tokenType) {
                case FUNCTION_NAME:
                    tokenBuffer.returnBack();
                    return function(tokenBuffer);

                case OPERATOR_SUB:
                    return -factor(tokenBuffer);

                case NUMBER:
                    return Double.parseDouble(token.data);

                case LEFT_BRACKET:
                    double value = expression(tokenBuffer);
                    token = tokenBuffer.getNextToken();
                    if (token.tokenType != TokenType.RIGHT_BRACKET) {
                        throw new MathParserException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                    }
                    return value;

                default:
                    throw new MathParserException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
            }
        }

        public static double function(final TokenBuffer tokenBuffer) throws MathParserException {
            String function = tokenBuffer.getNextToken().data;
            Token token = tokenBuffer.getNextToken();

            if (token.tokenType != TokenType.LEFT_BRACKET) {
                throw new MathParserException();
            }

            ArrayList<Double> args = new ArrayList<>();
            token = tokenBuffer.getNextToken();

            if (token.tokenType != TokenType.RIGHT_BRACKET) {
                tokenBuffer.returnBack();
                do {

                    args.add(expression(tokenBuffer));
                    token = tokenBuffer.getNextToken();

                    if ((token.tokenType != TokenType.COMMA) && (token.tokenType != TokenType.RIGHT_BRACKET)) {
                        throw new MathParserException();
                    }

                } while (token.tokenType == TokenType.COMMA);
            }
            return MathFunctions.FUNCTION_MAP.get(function).apply(args);
        }
    }
}
