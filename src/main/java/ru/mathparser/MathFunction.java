package ru.mathparser;

import java.util.List;

@FunctionalInterface
public interface MathFunction {
    double apply(List<Double> args);
}