package ru.grapher;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.xy.XYSeries;

public final class GrapherUtilities {

    private GrapherUtilities() throws AssertionError {
        throw new AssertionError();
    }

    public static boolean isCorrectValue(final double value) {
        return Math.abs(value) <= Grapher.RunConfiguration.COMPUTATION_BOUND;
    }

    public static NumberTickUnit getNormalNumberTickUnit(final NumberAxis axis,
                                                         final String orientation) {
        if (orientation.equals("x"))
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / Grapher.RunConfiguration.NUMBER_OF_TICKS / 2.0);
        else if (orientation.equals("y"))
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR) / 2.0);
        else
            return null;
    }

    public static void normalizeTick(NumberAxis axis,
                                     final String orientation) {
        axis.setTickUnit(getNormalNumberTickUnit(axis, orientation));
    }

    public static void resetSlider(final SteppingSlider<?> slider) {
        slider.setValue(slider.getDomainValues().size() / 2);
    }

    public static void resetAxes(JFreeChart chart) {
        NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
        NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();

        yAxis.centerRange(0);
        yAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y.getUpperBound() / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X.getUpperBound() / Grapher.RunConfiguration.NUMBER_OF_TICKS));

        Grapher.setModifiedLowerY(-Grapher.RunConfiguration.DEFAULT_Y);
        Grapher.setModifiedUpperY(Grapher.RunConfiguration.DEFAULT_Y);
        Grapher.setModifiedLowerX(-Grapher.RunConfiguration.DEFAULT_X);
        Grapher.setModifiedUpperX(Grapher.RunConfiguration.DEFAULT_X);

        chart.getXYPlot().setRangeCrosshairValue(0);
        chart.getXYPlot().setDomainCrosshairValue(0);

        yAxis.centerRange(0);
        yAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_Y.getUpperBound() / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_RANGE_OF_X.getUpperBound() / Grapher.RunConfiguration.NUMBER_OF_TICKS));
    }

    public static void updateMinMax(final XYSeries series) throws NoSuchFieldException, IllegalAccessException {

//        rangeMaxValues.add(series.getMaxY());
//        rangeMinValues.add(series.getMinY());
//        rangeMinValuesMinimum = series.getMinY();
//        rangeMaxValuesMinimum = series.getMaxY();
//
//        if (isCorrectValue(series.getMaxY()))
//            rangeMaxValuesMinimum = rangeMaxValues.stream().min(Double::compare).orElse(1.0);
//        else
//            rangeMaxValuesMinimum = COMPUTATION_BOUND;
//
//        if (isCorrectValue(series.getMinY()))
//            rangeMinValuesMinimum = rangeMinValues.stream().min(Double::compare).orElse(-1.0);
//        else
//            rangeMinValuesMinimum = -COMPUTATION_BOUND;
    }
}
