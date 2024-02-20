package ru.mathparser;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class MathParser implements MathConstants {

    private static final List<String> SORTED_NAMES = new ArrayList<>();

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
            String value = Precision.format(MATH_CONSTANTS.get(name));

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
            MathFunctionParser.isDeclaredImplicit(changed);
        } catch (StringIndexOutOfBoundsException e) {
            return ParsingResult.ERROR;
        }

        Exception last = null;
        String pf = changed;

        try {
            var coefficients = MathFunctionParser.Parametric.getCoefficients(changed);
            for (var c: coefficients) {
                pf = MathFunctionParser.Parametric.replaceCoefficient(pf, c, 1.0);
            }
            parseNoHandling(pf);
        } catch (Exception e) {
            last = e;
        }

        if (last == null && !MathFunctionParser.Explicit.isFunction(pf))
            return ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS;

        if (MathFunctionParser.Parametric.isFunction(changed)) {
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
                pchanged = MathFunctionParser.Parametric.replaceVariable(pchanged, t);
                parseNoHandling(pchanged);
            } catch (Exception e) {

                exception = e;
                Exception err = null;

                try {
                    for (var s: MathFunctionParser.Parametric.getCoefficients(pchanged))
                        pchanged = MathFunctionParser.Parametric.replaceCoefficient(pchanged, s, t);

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

        if (!MathFunctionParser.isDeclaredImplicit(changed)) {
            final double x = 1.0;

            try {
                parseNoHandling(expression);
            } catch (IndexOutOfBoundsException e) {
                return ParsingResult.ERROR;
            } catch (MathParserException notExpression) {

                try {
                    changed = MathFunctionParser.Explicit.replaceVariable(changed, x);

                    if (changed.isEmpty()) {
                        return ParsingResult.ERROR;
                    }

                    double ignored = parseNoHandling(changed);
                } catch (MathParserException notFunction) {

                    try {

                        for (var coefficient : MathFunctionParser.Explicit.getCoefficients(changed)) {
                            changed = MathFunctionParser.Explicit.replaceCoefficient(changed, coefficient, x);
                        }

                        double ignored = parseNoHandling(changed);

                    } catch (IllegalArgumentException | IndexOutOfBoundsException | MathParserException e) {
                        return ParsingResult.ERROR;

                    }

                    return ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS;
                } catch (IllegalArgumentException | IndexOutOfBoundsException ill) {
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
        } catch (MathParserException ignored) {

        }

        TokenBuffer tokenBuffer = new TokenBuffer(tokenList);

        try {
            return Syntax.expression(tokenBuffer);
        } catch (MathParserException ignored) {

        }

        return 0;
    }

    public static List<Token> tokenize(String expression) throws MathParserException {
        List<Token> tokenList = new ArrayList<>();

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
                        throw new MathParserTokenizeException("Illegal brackets");
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
                    } else {
                        tokenList.add(new Token(TokenType.OPERATOR_MUL, character));
                        pos++;
                    }
                    continue;
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
                                    throw new MathParserTokenizeException("Unexpected function: " + function);
                                }

                            } else {
                                throw new MathParserTokenizeException("Unexpected character: '" + character + "' at pos " + pos + " in expression");
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }

        if (!stack.isEmpty()) {
            throw new MathParserTokenizeException("Bracket count don't match");
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

    public static final class Syntax {

        private Syntax() throws InstantiationException {
            throw new InstantiationException();
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
                        throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
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
                        throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
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

                    case
                            OPERATOR_MUL,
                            OPERATOR_DIV,
                            EOF,
                            RIGHT_BRACKET,
                            COMMA,
                            OPERATOR_ADD,
                            OPERATOR_SUB:

                        tokenBuffer.returnBack();
                        return value;

                    default:
                        throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
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
                        throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                    }
                    return value;

                default:
                    throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
            }
        }

        public static double function(final TokenBuffer tokenBuffer) throws MathParserException {
            String function = tokenBuffer.getNextToken().data;
            Token token = tokenBuffer.getNextToken();

            if (token.tokenType != TokenType.LEFT_BRACKET) {
                throw new MathParserSyntaxException();
            }

            List<Double> args = new ArrayList<>();
            token = tokenBuffer.getNextToken();

            if (token.tokenType != TokenType.RIGHT_BRACKET) {
                tokenBuffer.returnBack();
                do {

                    args.add(expression(tokenBuffer));
                    token = tokenBuffer.getNextToken();

                    if ((token.tokenType != TokenType.COMMA) && (token.tokenType != TokenType.RIGHT_BRACKET)) {
                        throw new MathParserSyntaxException();
                    }

                } while (token.tokenType == TokenType.COMMA);
            }
            return MathFunctions.FUNCTION_MAP.get(function).apply(args);
        }
    }
}
