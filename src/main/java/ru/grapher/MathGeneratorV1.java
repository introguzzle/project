package ru.grapher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MathGeneratorV1 extends AbstractMathGenerator implements MathConstants {

    private static final ArrayList<String> MATH_CONSTANTS_LIST =
            new ArrayList<>(MATH_CONSTANTS.keySet());

    public MathGeneratorV1() {
        super();
    }

    public MathGeneratorV1(final int minExpressionLength,
                           final int maxExpressionLength,
                           final double minNumber,
                           final double maxNumber,
                           final int maxArgs,
                           final int depthForwardProbability,
                           final int complicationProbability,
                           final int numOrCoeffProbability) {
        super(minExpressionLength, maxExpressionLength, minNumber, maxNumber, maxArgs, depthForwardProbability, complicationProbability, numOrCoeffProbability);
    }

    protected String generateOperator() {
        int number = randomInRange(0, 4);

        return switch (number) {
            case 0  -> "+";
            case 1  -> "-";
            case 2  -> "*";
            case 3  -> "/";
            case 4  -> "^";
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    protected String generateNumber() {
        DecimalFormat format = new DecimalFormat("#.##");
        double number = randomInRange(this.minNumber, this.maxNumber);

        if (guess(75)) {
            if (number > 0)
                return format.format(number).replace(",", ".");
            else {
                return "(" + format.format(number).replace(",", ".") + ")";
            }
        } else
            return MATH_CONSTANTS_LIST.get(randomInRange(0, MATH_CONSTANTS_LIST.size() - 1));

    }

    protected String generatePositiveNumber() {
        DecimalFormat format = new DecimalFormat("#.##");
        double number = randomInRange(0, this.maxNumber);

        return format.format(number).replace(",", ".");
    }

    protected String generateCoefficient() {
        return String.valueOf(AVAILABLE_COEFFICIENTS.charAt(
                ThreadLocalRandom.current().nextInt(0, AVAILABLE_COEFFICIENTS.length() - 1)));
    }

    private int getArgsSize(String mathFunction) {
        String required = MathFunctions.REQUIRED_ARGS.get(mathFunction);
        int _args = 0;

        if (required.charAt(0) == '+') {
            _args = randomInRange(Integer.parseInt(String.valueOf(required.charAt(1))), this.maxArgs);
        } else {
            _args = Integer.parseInt(String.valueOf(required.charAt(0)));
        }

        return _args;
    }

    private static boolean isNumber(String arg) {
        String string = arg.replace(".", "")
                .replace("-", "")
                .replace(")", "")
                .replace("(", "");

        for (var i: string.toCharArray()) {
            if (!Character.isDigit(i))
                return false;
        }

        return true;
    }

    private static boolean isNegativeNumber(String arg) {
        if (!isNumber(arg))
            throw new IllegalArgumentException();
        else {
            return arg.contains("-");
        }
    }

    private static boolean isFunctionDefinedOnNegative(String function) {
        return !(
                function.equals("log") || function.equals("ln")
                || function.equals("sqrt") || function.equals("cbrt")
                || function.equals("rand")
        );
    }

    private String generateMathFunctionWithNumbers() {
        StringBuilder result      = new StringBuilder();
        String        function    = generateMathFunctionHeader();

        boolean allowNegativeArguments  = isFunctionDefinedOnNegative(function);
        int     _args                   = getArgsSize(function);

        result.append(function).append("(");

        for (int i = 0; i < _args; i++) {
            if (!guess(this.depthForwardProbability))
                if (allowNegativeArguments)
                    result.append(generateNumber());
                else
                    result.append(generatePositiveNumber());
            else
                result.append(generateMathFunctionWithNumbers());

            if (i != _args - 1)
                result.append(", ");
            else
                result.append(")");
        }

        return result.toString();
    }

    private String generateMathFunctionWithVariable(final Character variable,
                                                   final boolean includeCoefficients) {
        StringBuilder result      = new StringBuilder();
        String        function    = generateMathFunctionHeader();

        int             _args       = getArgsSize(function);

        result.append(function).append("(");

        for (int i = 0; i < _args; i++) {
            if (!guess(this.depthForwardProbability))
                if (!guess(this.complicationProbability))
                    result.append(variable);
                else
                    result.append(generateSimpleVariableExpression(variable, includeCoefficients));
            else
                result.append(generateMathFunctionWithVariable(variable, includeCoefficients));

            if (i != _args - 1)
                result.append(", ");
            else
                result.append(")");
        }

        return result.toString();
    }

    protected static String generateMathFunctionHeader() {
        return AbstractMathGenerator.generateMathFunctionHeader();
    }

    private String generateSimpleVariableExpression(final Character variable,
                                                    final boolean includeCoefficients) {
        int exprLength = randomInRange(this.minExpressionLength, this.maxExpressionLength);
        StringBuilder result = new StringBuilder();
        int leftCount = 0;

        for (int i = 0; i < exprLength; i++) {

            if (guess(this.complicationProbability)) {
                result.append("(");
                leftCount++;

                result.append(variable);
                result.append(" ").append(generateOperator()).append(" ");

                if (includeCoefficients)
                    if (!guess(this.numOrCoeffProbability))
                        result.append(generateSimpleExpression());
                    else
                        result.append(generateCoefficient());
                else
                    result.append(generateSimpleExpression());

                if (i != exprLength - 1)
                    result.append(" ").append(generateOperator()).append(" ");
            } else {
                result.append(variable);
                result.append(" ").append(generateOperator()).append(" ");

                if (includeCoefficients)
                    if (!guess(this.numOrCoeffProbability))
                        result.append(generateNumber());
                    else
                        result.append(generateCoefficient());
                else
                    result.append(generateNumber());

                if (i != exprLength - 1)
                    result.append(" ").append(generateOperator()).append(" ");
            }
        }

        for (int i = 0; i < leftCount; i++) {
            result.append(")");
        }

        return result.toString();
    }

    public String generateSimpleExpression() {
        final int exprLength = randomInRange(this.minExpressionLength, this.maxExpressionLength);
        StringBuilder result = new StringBuilder();
        int leftCount = 0;

        for (int i = 0; i < exprLength; i++) {

            if (!guess(this.complicationProbability)) {
                result.append("(");
                leftCount++;

                result.append(generatePositiveNumber());

                if (i != exprLength - 1)
                    result.append(" ").append(generateOperator()).append(" ");
            } else {
                result.append(generatePositiveNumber());

                if (i != exprLength - 1)
                    result.append(" ").append(generateOperator()).append(" ");
            }
        }

        for (int i = 0; i < leftCount; i++) {
            result.append(")");
        }

        return result.toString();
    }

    @Override
    public String generateExpression() {
        final int exprLength = randomInRange(this.minExpressionLength, this.maxExpressionLength);
        StringBuilder result = new StringBuilder();

        final boolean begin  = guess(this.complicationProbability);

        if (begin) {
            result.append(generateNumber());
        } else {
            result.append(generateMathFunctionWithNumbers());
        }

        for (int i = 0; i < exprLength; i++) {
            result.append(" ").append(generateOperator());

            if (!guess(50)) {
                result.append(" ").append(generateNumber());
            } else {
                result.append(" ").append(generateMathFunctionWithNumbers());
            }
        }

        return result.toString();
    }

    @Override
    public String generateFunction(final Character variable,
                                   final int length) {
        String first = "";

        if (ThreadLocalRandom.current().nextBoolean())
            first = generateMathFunctionWithVariable(variable, false);
        else
            first = generateSimpleExpression();

        final StringBuilder header = new StringBuilder();
        header.append("f(").append(variable).append(") = ");

        StringBuilder result = new StringBuilder(header);

        result.append(first);

        for (int i = 0; i < length; i++) {
            result.append(" ").append(generateOperator());

            final int pick = randomInRange(0, 6);

            switch (pick) {
                case 0          -> result.append(" ").append(generateExpression());
                case 1, 2, 3, 4 -> result.append(" ").append(generateMathFunctionWithVariable(variable, false));
                case 5          -> result.append(" ").append(generateNumber());
                case 6          -> result.append(" ").append(generateSimpleVariableExpression(variable, false));

            }
        }

        return result.toString();
    }

    @Override
    public String generateFunctionWithCoefficients(final Character variable) {
        final String s = generateMathFunctionWithVariable(variable, true);

        return new StringBuffer(s).insert(0, "f(x) = ").toString();
    }
}
