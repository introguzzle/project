package ru.grapher;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MathGeneratorV2 extends AbstractMathGenerator implements MathConstants {

    private static final ArrayList<String> MATH_CONSTANTS_LIST =
            new ArrayList<>(MATH_CONSTANTS.keySet());

    public MathGeneratorV2() {
        super();
    }

    public MathGeneratorV2(final int minExpressionLength,
                           final int maxExpressionLength,
                           final double minNumber,
                           final double maxNumber,
                           final int maxArgs,
                           final int depthForwardProbability,
                           final int complicationProbability,
                           final int numOrCoeffProbability) {
        super(minExpressionLength, maxExpressionLength, minNumber, maxNumber, maxArgs, depthForwardProbability, complicationProbability, numOrCoeffProbability);
    }

    @Override
    protected String generateOperator() {
        final int pick = randomInRange(0, 4);

        return switch (pick) {
            case 0 -> " + ";
            case 1 -> " - ";
            case 2 -> " * ";
            case 3 -> " / ";
            case 4 -> " ^ ";
            // SHOULD NEVER HAPPEN
            default -> throw new NullPointerException("This exception should never be thrown");
        };
    }

    @Override
    protected String generateNumber() {
        DecimalFormat format = new DecimalFormat("#.##");

        double number = randomInRange(this.minNumber, this.maxNumber);

        boolean z = number >= -1.0 && number <= 1.0;

        if (number > 0)
            if (guess(50))
                return format.format(number).replace(",", ".");
            else
                return !z ? format.format((int) number).replace(",", ".")
                        : format.format(number).replace(",", ".");


        else {
            if (guess(50))
                return "(" + format.format(number).replace(",", ".") + ")";
            else
                return !z ? "(" + format.format((int) number).replace(",", ".") + ")"
                        : "(" + format.format(number).replace(",", ".") + ")";
        }
    }

    public String generateConstant() {
        final int pick = randomInRange(0, MATH_CONSTANTS_LIST.size() - 1);

        return MATH_CONSTANTS_LIST.get(pick);
    }

    private String generateExpression(final boolean includeFunctions) {
        StringBuilder result = new StringBuilder();

        int length = randomInRange(this.minExpressionLength, this.maxExpressionLength) / 3 + 1;

        System.out.println(length);

        if (length < 1) {
            length = 1;
        }

        if (includeFunctions)
            if (guess(0))
                result.append(generateNumber());
            else
                result.append(generateConstant());
        else
            if (guess(0))
                result.append(generateNumber());
            else
                result.append(generateConstant());


        for (int i = 0; i <= length; i++) {
            result.append(generateOperator());

            if (includeFunctions)
                if (guess(0))
                    result.append(generateNumber());
                else
                    result.append(generateConstant());
            else
                if (guess(0))
                    result.append(generateNumber());
                else
                    result.append(generateConstant());
        }

        return result.toString();
    }

    public String generateMathFunction() {
        StringBuilder result = new StringBuilder();

        final String header     = generateMathFunctionHeader();
        final String req        = MathFunctions.REQUIRED_ARGS.get(header);

        final int    required   = req.charAt(0) == '+'
                ? this.maxArgs
                : Integer.parseInt(Character.toString(req.charAt(0))
        );

        final int    initDepth  = this.depthForwardProbability;

        result.append(header);

        int i = 0;

        while (i < required) {
            result.append("(");

            if (guess(initDepth))
                result.append(generateMathFunction());
            else
                result.append(generateNumber());

            if (i != 0)
                result.append(", ");
            i++;
        }

        return result.toString();
    }

    @Override
    public String generateExpression() {
        return generateExpression(false);
    }

    @Override
    public String generateFunction(final Character variable,
                                   final int length) {
        return null;
    }

    @Override
    public String generateFunctionWithCoefficients(final Character variable) {
        return null;
    }
}
