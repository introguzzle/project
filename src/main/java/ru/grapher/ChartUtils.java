package ru.grapher;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import ru.grapher.slider.SteppingSlider;

public final class ChartUtils {

    private ChartUtils() throws InstantiationException {
        throw new InstantiationException();
    }

    public static NumberTickUnit getNormalNumberTickUnit(final NumberAxis axis,
                                                         final boolean x) {
        double dist   = Math.abs(axis.getUpperBound() - axis.getLowerBound());
        double ticks  = Grapher.RunConfiguration.NUMBER_OF_TICKS;

        double factor = Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR;

        return x
            ? new NumberTickUnit(dist / ticks / 2.0)
            : new NumberTickUnit(dist / (ticks / factor) / 2.0);
    }

    public static void normalizeTick(final NumberAxis axis,
                                     boolean x) {
        axis.setTickUnit(getNormalNumberTickUnit(axis, x));
    }

    public static void resetSlider(final SteppingSlider<?> slider) {
        slider.setValue(slider.getDomainValues().size() / 2);
    }

    public static void resetAxes(final Grapher grapher,
                                 final JFreeChart chart) {
        NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
        NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();

        yAxis.centerRange(0);
        yAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y.getUpperBound() / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X.getUpperBound() / Grapher.RunConfiguration.NUMBER_OF_TICKS));

        grapher.currentLowerY = -Grapher.RunConfiguration.DEFAULT_Y;
        grapher.currentUpperY = Grapher.RunConfiguration.DEFAULT_Y;
        grapher.currentLowerX = -Grapher.RunConfiguration.DEFAULT_X;
        grapher.currentUpperX = Grapher.RunConfiguration.DEFAULT_X;

        chart.getXYPlot().setRangeCrosshairValue(0);
        chart.getXYPlot().setDomainCrosshairValue(0);

        yAxis.centerRange(0);
        yAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y.getUpperBound() / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X.getUpperBound() / Grapher.RunConfiguration.NUMBER_OF_TICKS));
    }
}
