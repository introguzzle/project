package ru.grapher;

public class MathGeneratorBuilder {

    private int minExpressionLength;
    private int maxExpressionLength;

    private double minNumber;
    private double maxNumber;

    private int maxArgs;

    private int depthForwardProbability;
    private int complicationProbability;
    private int numOrCoeffProbability;

    public final MathGeneratorBuilder setMinExpressionLength(int minExpressionLength) {
        this.minExpressionLength = minExpressionLength;
        return this;
    }

    public final MathGeneratorBuilder setMaxExpressionLength(int maxExpressionLength) {
        this.maxExpressionLength = maxExpressionLength;
        return this;
    }

    public final MathGeneratorBuilder setMinNumber(double minNumber) {
        this.minNumber = minNumber;
        return this;
    }

    public final MathGeneratorBuilder setMaxNumber(double maxNumber) {
        this.maxNumber = maxNumber;
        return this;
    }


    public final MathGeneratorBuilder setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
        return this;
    }

    public final MathGeneratorBuilder setDepthForwardProbability(int depthForwardProbability) {
        this.depthForwardProbability = depthForwardProbability;
        return this;
    }

    public final MathGeneratorBuilder setComplicationProbability(int complicationProbability) {
        this.complicationProbability = complicationProbability;
        return this;
    }

    public final MathGeneratorBuilder setNumOrCoeffProbability(int numOrCoeffProbability) {
        this.numOrCoeffProbability = numOrCoeffProbability;
        return this;
    }

    public final AbstractMathGenerator build(int version) {
        return switch(version) {
            case 1 -> new MathGeneratorV1(
                    minExpressionLength,
                    maxExpressionLength,
                    minNumber,
                    maxNumber,
                    maxArgs,
                    depthForwardProbability,
                    complicationProbability,
                    numOrCoeffProbability
            );
            case 2 -> new MathGeneratorV2(
                    minExpressionLength,
                    maxExpressionLength,
                    minNumber,
                    maxNumber,
                    maxArgs,
                    depthForwardProbability,
                    complicationProbability,
                    numOrCoeffProbability
            );
            default -> throw new IllegalArgumentException("Version doesn't exist: " + version);
        };
    }

//    public final MathGeneratorV1 getV1() {
//        return new MathGeneratorV1(
//                minExpressionLength,
//                maxExpressionLength,
//                minNumber,
//                maxNumber,
//                maxArgs,
//                depthForwardProbability,
//                complicationProbability,
//                numOrCoeffProbability
//        );
//    }
}
