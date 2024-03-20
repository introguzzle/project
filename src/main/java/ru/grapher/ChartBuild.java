package ru.grapher;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import ru.grapher.core.DelayedMouseListener;
import ru.grapher.slider.CoefficientSlider;

import java.awt.*;
import java.awt.event.MouseAdapter;

public class ChartBuild {

    static JFreeChart createChart(XYSeriesCollection listenedXYSeriesCollection) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "x",
                "y",
                listenedXYSeriesCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.BLACK);
        plot.setRangeCrosshairStroke(new BasicStroke(1.5f));

        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairPaint(Color.BLACK);
        plot.setDomainCrosshairStroke(new BasicStroke(1.5f));

        plot.setRangeCrosshairLockedOnData(false);
        plot.setDomainCrosshairLockedOnData(false);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setRange(-Grapher.RunConfiguration.DEFAULT_X, Grapher.RunConfiguration.DEFAULT_X);
        xAxis.setAutoTickUnitSelection(true);
        xAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_X / Grapher.RunConfiguration.NUMBER_OF_TICKS));

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(-Grapher.RunConfiguration.DEFAULT_Y, Grapher.RunConfiguration.DEFAULT_Y);
        yAxis.setAutoTickUnitSelection(true);
        yAxis.setTickUnit(new NumberTickUnit(Grapher.RunConfiguration.DEFAULT_Y / (Grapher.RunConfiguration.NUMBER_OF_TICKS / Grapher.RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, new Color(
                (int) (Math.random() * 100),
                (int) (Math.random() * 100),
                (int) (Math.random() * 100))
        );

        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(0, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        return chart;
    }
}
