package ru.grapher;

import org.jfree.chart.axis.*;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;

import ru.calculator.Calculator;

import ru.grapher.addbutton.FunctionDialog;
import ru.grapher.core.*;
import ru.grapher.core.EventListener;
import ru.grapher.exit.ExitDialog;
import ru.grapher.range.RangeDialog;
import ru.grapher.slider.CoefficientSlider;

import ru.mathparser.MathFunctionParser;

import java.awt.event.*;
import java.util.*;

public class Grapher extends StatelessGrapher {

    public static final class RunConfiguration {
        public static final boolean ASK_CONFIRMATION        = true;

        public static final double  NUMBER_OF_TICKS         = 10.0;
        public static final double  SCALE_TO_SQUARE_FACTOR  = 1.25;
        // 1024 / 768 is 1.33, but 1.33 is odd value, so using 5 / 4 resolution instead of 4 / 3

        public static final double  DEFAULT_X               = 10.0;
        public static final Range   DEFAULT_RANGE_OF_X      = new Range(-DEFAULT_X, DEFAULT_X);
        public static final double  DEFAULT_Y               = 8.0;
        public static final Range   DEFAULT_RANGE_OF_Y      = new Range(-DEFAULT_Y, DEFAULT_Y);

        // we define this like this cuz we don't need to compute X values
        // outside visible plot
        // for example: default X is 8 and maximum scope is 1000%
        // so bound of computation is [-80, 80]
    }

    double currentScope       = 1.0;
    String currentCoefficient = null;

    double xShift = 1.0;
    double yShift = 1.0;

    double currentLowerY = -RunConfiguration.DEFAULT_Y;
    double currentUpperY = RunConfiguration.DEFAULT_Y;

    double currentLowerX = -RunConfiguration.DEFAULT_X;
    double currentUpperX = RunConfiguration.DEFAULT_X;

    public Grapher() {
        super();

        initComponentActions();
    }

    void initComponentActions() {
        this.initBoxActions();
        this.initButtonActions();
        this.initSliderActions();

        this.initWindowListener();
        this.initKeyListener();
    }

    private void initBoxActions() {
        coefficientBox.setLinkedComponent(coefficientSlider);
    }

    private void initSliderActions() {
        MouseListener coefficientSliderMouseListener = new DelayedMouseListener((e) -> {
            if (coefficientSlider.isEnabled()) {
                currentCoefficient = coefficientBox.getSelectedItem();
                coefficientMap.put(
                        currentCoefficient,
                        coefficientSlider.getDomainValue() * CoefficientSlider.VALUE_MULTIPLIER
                );

                updateXYSeriesCollection();
            }
        });

        coefficientSlider.addMouseListener(coefficientSliderMouseListener);

        scopeSlider.addChangeListener(e -> {
            currentScope = scopeSlider.getDomainValue() / 100.0;

            NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
            NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

            double lx = currentLowerX * currentScope;
            double ux = currentUpperX * currentScope;

            if (Compute.isCorrectValue(lx) && Compute.isCorrectValue(ux)) {
                xAxis.setRange(new Range(lx, ux));
                yAxis.setRange(new Range(currentLowerY * currentScope, currentUpperY * currentScope));
            }

            ChartUtils.normalizeTick(xAxis, true);
            ChartUtils.normalizeTick(yAxis, false);

            xShift = ChartUtils.getNormalNumberTickUnit(xAxis, true).getSize();
            yShift = ChartUtils.getNormalNumberTickUnit(yAxis, false).getSize();
        });

        coefficientBox.addActionListener(evt -> {
            double value = coefficientMap.get(coefficientBox.getSelectedItem());
            coefficientSlider.setClosestDomainValue(value);
        });
    }

    private void initWindowListener() {
        if (RunConfiguration.ASK_CONFIRMATION) {
            this.addWindowListener(new WindowClosingListener((event)
                    -> new ExitDialog(this)
            ));
        }
    }

    private void initKeyListener() {
        this.addKeyListener(new KeyAdapter() {
            public void shift(boolean sign) {
                coefficientSlider.setValue(coefficientSlider.getValue() + (sign ? 1 : -1));
                currentCoefficient = coefficientBox.getSelectedItem();
                coefficientMap.put(
                        currentCoefficient,
                        coefficientSlider.getDomainValue() * CoefficientSlider.VALUE_MULTIPLIER
                );

                updateXYSeriesCollection();
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                final int KEY = evt.getKeyCode();

                if (coefficientSlider.isEnabled()) {
                    if (KEY == KeyEvent.VK_MINUS)
                        shift(false);

                    if (KEY == KeyEvent.VK_EQUALS)
                        shift(true);
                }
            }
        });

        this.addKeyListener(new GrapherKeyListener(this));
    }

    private void initButtonActions() {
        addButton.addActionListener(this::addAction);
        clearButton.addActionListener(this::clearAction);
        resetButton.addActionListener(this::resetAction);
        rangeButton.addActionListener(this::rangeAction);
        calculatorButton.addActionListener(this::calculatorAction);
    }

    private void calculatorAction(ActionEvent event) {
        Calculator.run(this);
    }

    private void rangeAction(ActionEvent event) {
        new EventListener(e -> new RangeDialog(event, coefficientSlider.getValues()));
    }

    private void resetAction(ActionEvent event) {
        ChartUtils.resetAxes(this, chart);
        ChartUtils.resetSlider(scopeSlider);
    }

    private void addAction(ActionEvent event) {
        new FunctionDialog(this);
    }

    private void clearAction(ActionEvent event) {
        currentXYSeriesCollection.removeAllSeries();
        currentXYSeriesMap.clear();

        resetAction(event);

        coefficientBox.setEnabled(false);
        coefficientBox.removeAllItems();

        coefficientSlider.setEnabled(false);
    }

    public void addToXYSeriesCollection(final XYSeries series) {
        Set<String> coefficients = MathFunctionParser.Explicit.getCoefficients(series.getDescription());

        currentXYSeriesCollection.addSeries(series);
        currentXYSeriesMap.put(series, coefficients);
    }

    public void addParametricToXYSeriesCollection(final XYSeries series) {
        Set<String> coefficients = MathFunctionParser.Explicit.getCoefficients(series.getDescription());

        currentXYSeriesCollection.addSeries(series);
        currentXYSeriesMap.put(series, coefficients);
    }

    public void updateXYSeriesCollection() {
        currentXYSeriesCollection.removeAllSeries();

        for (var entry: currentXYSeriesMap.entrySet()) {
            try {
                XYSeries series;

                String description = entry.getKey().getDescription();

                if (description.contains("x(t)")) {
                    String[] t = resolveParametricFunction(description);
                    series = Compute.createParametricXYSeries(t, coefficientMap);

                } else {
                    series = Compute.createXYSeries(description, coefficientMap);
                }

                currentXYSeriesCollection.addSeries(series);
            } catch (IllegalArgumentException ignored) {

            }
        }
    }

    private static String[] resolveParametricFunction(String description) {
        String x = description.substring(
                    description.indexOf("=") + 2,
                    description.indexOf("y(t)") - 2);

        String y = description.substring(description.lastIndexOf("=") + 2);

        return new String[] {x, y};
    }
}