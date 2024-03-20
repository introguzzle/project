package ru.grapher.addbutton;

import ru.mathparser.AbstractMathGenerator;
import ru.mathparser.MathGeneratorBuilder;

public class GeneratorHolder {

    public static final AbstractMathGenerator DEFAULT_GENERATOR = new MathGeneratorBuilder()
            .setMinExpressionLength(1)
            .setMaxExpressionLength(3)
            .setMinNumber(-10)
            .setMaxNumber(10)
            .setMaxArgs(3)
            .setDepthForwardProbability(40)
            .setComplicationProbability(60)
            .setNumOrCoeffProbability(50)
            .build(1);

    public static final AbstractMathGenerator PARAMETRIC_GENERATOR = new MathGeneratorBuilder()
            .setMinExpressionLength(1)
            .setMaxExpressionLength(3)
            .setMinNumber(-10)
            .setMaxNumber(10)
            .setMaxArgs(3)
            .setDepthForwardProbability(40)
            .setComplicationProbability(30)
            .setNumOrCoeffProbability(50)
            .build(1);
}
