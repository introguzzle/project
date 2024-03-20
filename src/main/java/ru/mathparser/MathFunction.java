package ru.mathparser;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface MathFunction extends Function<List<Double>, Double> {

}