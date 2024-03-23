package ru.grapher;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import ru.grapher.slider.ScopeSlider;
import ru.mathparser.MathFunctionParser;
import ru.mathparser.MathParser;
import ru.mathparser.MathParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.grapher.Grapher.RunConfiguration.DEFAULT_X;

public final class Compute {

    private Compute() throws InstantiationException {
        throw new InstantiationException();
    }

    private static final int    PRECISION_DIGITS = 2;
    private static final double COMPUTATION_BOUND = (double) ScopeSlider.DefaultConfiguration.
            SCOPE_DOMAIN_VALUES.getLast() * DEFAULT_X / 100.0;


    private static final List<Double> X_VALUES = new ArrayList<>();

    static {
        final double step = 1 / Math.pow(10, PRECISION_DIGITS);

        for (double d = -COMPUTATION_BOUND;
             d <= COMPUTATION_BOUND;
             d += step) {
            X_VALUES.add(d);
        }
    }

    private static double
    parametricCompute(final String expression,
                      final double value,
                      final Map<String, Double> coefficientMap) {
        String r = MathFunctionParser.Parametric.transform(expression, coefficientMap, value);
        return MathParser.parse(r);
    }

    private static double
    compute(final String expression,
            final double value,
            final Map<String, Double> coefficientMap) {
        String r = MathFunctionParser.Explicit.transform(expression, coefficientMap, value);
        return MathParser.parse(r);
    }

    public static
    XYSeries createXYSeries(final String function,
                            final Map<String, Double> coefficientMap) {
        XYSeries series = new XYSeries(function, true, true);
        series.setDescription(function);

        X_VALUES.stream().parallel().forEachOrdered(x -> {
            try {
                series.add(x, (Double) compute(function, x, coefficientMap));
            } catch (IndexOutOfBoundsException | MathParserException ignored) {

            }
        });

        return series;
    }

    public static
    XYSeries createParametricXYSeries(final String[] f,
                                      final Map<String, Double> coefficientMap) {
        return createParametricXYSeries(f[0], f[1], coefficientMap);
    }

    public static
    XYSeries createParametricXYSeries(final String xt,
                                      final String yt,
                                      final Map<String, Double> coefficientMap) {
        String key = "x(t) = " + xt + ", y(t) = " + yt;

        XYSeries series = new XYSeries(key, false, true);
        series.setDescription(key);

        List<XYDataItem> values = new ArrayList<>();

        X_VALUES.stream().parallel().forEachOrdered(t -> {
            try {
                values.add(new XYDataItem(
                        (Double) parametricCompute(xt, t, coefficientMap),
                        (Double) parametricCompute(yt, t, coefficientMap))
                );

            } catch (IndexOutOfBoundsException | MathParserException ignored) {

            }
        });

        values.parallelStream().forEachOrdered(series::add);

        return series;
    }

    public static boolean isCorrectValue(final double value) {
        return Math.abs(value) <= COMPUTATION_BOUND;
    }
}
