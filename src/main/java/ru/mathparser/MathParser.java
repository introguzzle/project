package ru.mathparser;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class MathParser implements MathConstants {

    private MathParser() throws InstantiationException {
        throw new InstantiationException();
    }

    public static String replaceConstants(final String function) {
        return Parser.replace(function, MATH_CONSTANTS, 0, function.length());
    }

    public static boolean isParsable(final String expression) {
        return getParsingResult(expression) == ParsingResult.EXPRESSION;
    }

    public static ParsingResult getParsingResult(final String expression) {
        if (expression == null || expression.isEmpty() || expression.isBlank()) {
            return ParsingResult.ERROR;
        }

        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(expression)) {
            return ParsingResult.ERROR;
        }

        Throwable exception = null;

        try {
            MathParser.uncheckedParse(MathParser.replaceConstants(expression));
        } catch (MathParserException e) {
            exception = e;
        }

        if (exception == null)
            return ParsingResult.EXPRESSION;

        if (!MathFunctionParser.Explicit.isNotFunction(expression))
            if (MathFunctionParser.Explicit.hasCoefficients(expression))
                return (MathFunctionParser.Explicit.isValid(expression))
                        ? ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMETERS
                        : ParsingResult.ERROR;
            else
                return (MathFunctionParser.Explicit.isValid(expression))
                        ? ParsingResult.EXPLICIT_FUNCTION
                        : ParsingResult.ERROR;

        if (MathFunctionParser.Parametric.isFunction(expression))
            if (MathFunctionParser.Parametric.hasCoefficients(expression))
                return (MathFunctionParser.Parametric.isValid(expression))
                        ? ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS
                        : ParsingResult.ERROR;
            else
                return (MathFunctionParser.Parametric.isValid(expression))
                        ? ParsingResult.PARAMETRIC_FUNCTION
                        : ParsingResult.ERROR;

        try {
            MathParser.uncheckedParse(MathParser.replaceConstants(expression));
        } catch (MathParserException e) {
            return ParsingResult.ERROR;
        }

        return ParsingResult.ERROR;
    }

    public static double uncheckedParse(final String expression) throws MathParserException {
        var t = tokenize(replaceConstants(expression));

        return Syntax.expression(new TokenBuffer(t));
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

        return Double.NaN;
    }

    private static List<Token> tokenize(String expression) {
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
                    if (pos + 1 != expression.length() && expression.charAt(pos + 1) == '*') {
                        tokenList.add(new Token(
                                TokenType.OPERATOR_EXP,
                                character + String.valueOf(expression.charAt(pos + 1)))
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

    private static final class Syntax {

        private Syntax() throws InstantiationException {
            throw new InstantiationException();
        }

        public static double expression(final TokenBuffer tokenBuffer) {
            Token token = tokenBuffer.getNextToken();
            if (token.getTokenType() == TokenType.EOF) {
                return 0;
            } else {
                tokenBuffer.returnBack();
                return addSubtract(tokenBuffer);
            }
        }

        public static double addSubtract(final TokenBuffer tokenBuffer) {
            double value = multiplyDivide(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.getTokenType()) {
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

        public static double multiplyDivide(final TokenBuffer tokenBuffer) {
            double value = exp(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.getTokenType()) {
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

        public static double exp(final TokenBuffer tokenBuffer) {
            double value = factor(tokenBuffer);
            while (true) {
                Token token = tokenBuffer.getNextToken();
                switch (token.getTokenType()) {
                    case OPERATOR_EXP:
                        value = Math.pow(value, exp(tokenBuffer));
                        break;

                    case OPERATOR_MUL:
                    case OPERATOR_DIV:
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

        public static double factor(final TokenBuffer tokenBuffer) {
            Token token = tokenBuffer.getNextToken();

            switch (token.getTokenType()) {
                case FUNCTION_NAME:
                    tokenBuffer.returnBack();
                    return function(tokenBuffer);

                case OPERATOR_SUB:
                    return -factor(tokenBuffer);

                case NUMBER:
                    return Double.parseDouble(token.getData());

                case LEFT_BRACKET:
                    double value = expression(tokenBuffer);
                    token = tokenBuffer.getNextToken();
                    if (token.getTokenType() != TokenType.RIGHT_BRACKET) {
                        throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
                    }

                    return value;

                default:
                    throw new MathParserSyntaxException("Unexpected token: '" + token.getTokenType() + "' at pos " + tokenBuffer.getPos() + " in expression");
            }
        }

        public static double function(final TokenBuffer tokenBuffer) {
            String function = tokenBuffer.getNextToken().getData();
            Token token = tokenBuffer.getNextToken();

            if (token.getTokenType() != TokenType.LEFT_BRACKET) {
                throw new MathParserSyntaxException();
            }

            List<Double> args = new ArrayList<>();
            token = tokenBuffer.getNextToken();

            if (token.getTokenType() != TokenType.RIGHT_BRACKET) {
                tokenBuffer.returnBack();
                do {

                    args.add(expression(tokenBuffer));
                    token = tokenBuffer.getNextToken();

                    if ((token.getTokenType() != TokenType.COMMA) && (token.getTokenType() != TokenType.RIGHT_BRACKET)) {
                        throw new MathParserSyntaxException();
                    }

                } while (token.getTokenType() == TokenType.COMMA);
            }

            return MathFunctions.FUNCTION_MAP.get(function).apply(args);
        }
    }
}
