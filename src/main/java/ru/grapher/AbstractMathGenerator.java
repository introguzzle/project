package ru.grapher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class AbstractMathGenerator {

    private static class DefaultConfiguration {

        protected DefaultConfiguration() throws InstantiationException {
            throw new InstantiationException();
        }

        protected static final int      DEFAULT_MAX_EXPRESSION_LENGTH             = 14;
        protected static final int      DEFAULT_MIN_EXPRESSION_LENGTH             = 6;

        protected static final double   DEFAULT_MIN_NUMBER                        = -10.0;
        protected static final double   DEFAULT_MAX_NUMBER                        = 10.0;

        protected static final int      DEFAULT_MAX_ARGS                          = 4;

        protected static final int      DEFAULT_DEPTH_PROBABILITY                 = 20;
        protected static final int      DEFAULT_COMPLICATION_PROBABILITY          = 25;
        protected static final int      DEFAULT_NUMBER_OR_COEFFICIENT_PROBABILITY = 50;
    }

    protected static final String AVAILABLE_COEFFICIENTS =
            "abcdghijklmnpqrstuvwz";

    protected static final ArrayList<String> FUNCTIONS =
            new ArrayList<>(MathFunctions.REQUIRED_ARGS.keySet());

    protected static final ArrayList<String> UNSAFE_FUNCTIONS =
            new ArrayList<>();

    static {
        FUNCTIONS.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {

            }
        });
    }

    protected int       minExpressionLength       = DefaultConfiguration.DEFAULT_MIN_EXPRESSION_LENGTH;
    protected int       maxExpressionLength       = DefaultConfiguration.DEFAULT_MAX_EXPRESSION_LENGTH;

    protected double    minNumber                 = DefaultConfiguration.DEFAULT_MIN_NUMBER;
    protected double    maxNumber                 = DefaultConfiguration.DEFAULT_MAX_NUMBER;

    protected int       maxArgs                   = DefaultConfiguration.DEFAULT_MAX_ARGS;

    protected int       depthForwardProbability   = DefaultConfiguration.DEFAULT_DEPTH_PROBABILITY;
    protected int       complicationProbability   = DefaultConfiguration.DEFAULT_COMPLICATION_PROBABILITY;
    protected int       numOrCoeffProbability     = DefaultConfiguration.DEFAULT_NUMBER_OR_COEFFICIENT_PROBABILITY;

    protected AbstractMathGenerator() {

    }

    protected AbstractMathGenerator(final int minExpressionLength,
                                    final int maxExpressionLength,
                                    final double minNumber,
                                    final double maxNumber,
                                    final int maxArgs,
                                    final int depthForwardProbability,
                                    final int complicationProbability,
                                    final int numOrCoeffProbability) {
        this.minExpressionLength        = minExpressionLength;
        this.maxExpressionLength        = maxExpressionLength;
        this.minNumber                  = minNumber;
        this.maxNumber                  = maxNumber;
        this.maxArgs                    = maxArgs;
        this.depthForwardProbability    = depthForwardProbability;
        this.complicationProbability    = complicationProbability;
        this.numOrCoeffProbability      = numOrCoeffProbability;
    }

    protected static int randomInRange(final int left,
                                       final int right) {
        return (int)(Math.random() * (right - left + 1)) + left;
    }

    protected static double randomInRange(final double left,
                                          final double right) {
        return ThreadLocalRandom.current().nextDouble(left, right);
    }

    protected static boolean guess(final int probability) {
        if (probability > 100 || probability < 0)
            throw new IllegalArgumentException("Argument must be in range [0, 100]");

        return randomInRange(0, 100) <= probability;
    }

    protected static boolean toss() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    protected static String generateMathFunctionHeader() {
        final int pick = randomInRange(0, FUNCTIONS.size() - 1);

        return FUNCTIONS.get(pick);
    }



    abstract protected String generateOperator();

    abstract protected String generateNumber();

    abstract public String generateExpression();

    abstract public String generateFunction(final Character variable,
                                            final int length);

    abstract public String generateFunctionWithCoefficients(final Character variable);



}
