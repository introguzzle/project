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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Grapher extends BasicGrapher {

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

    double currentLowerY = -RunConfiguration.DEFAULT_Y;
    double currentUpperY = RunConfiguration.DEFAULT_Y;

    double currentLowerX = -RunConfiguration.DEFAULT_X;
    double currentUpperX = RunConfiguration.DEFAULT_X;

    public Grapher() {
        super();
    }

    @Override
    void initComponents() {
        this.initBox();
        this.initButtons();
        this.initSliders();

        this.initWindowListener();
        this.initKeyListener();
    }

    private void initBox() {
        coefficientBox.setLinkedComponent(coefficientSlider);
    }

    private void initSliders() {
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

        scopeSlider.setPaintLabels(true);
        scopeSlider.setOrientation(JSlider.VERTICAL);
        scopeSlider.setFocusable(false);
        scopeSlider.setBackground(Color.WHITE);
        scopeSlider.setThumbColor(Color.RED);
        scopeSlider.setPreferredSize(new Dimension(20, GUI.SLIDER_HEIGHT));
        scopeSlider.setForeground(Color.BLACK);
        scopeSlider.setBorder(GUI.__UNIVERSAL_BORDER);
        scopeSlider.setFont(GUI.font(12));

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

            xShift = Objects.requireNonNull(ChartUtils.getNormalNumberTickUnit(
                    (NumberAxis) chart.getXYPlot().
                            getDomainAxis(), true)).getSize();

            yShift = Objects.requireNonNull(ChartUtils.getNormalNumberTickUnit(
                    (NumberAxis) chart.getXYPlot().
                            getRangeAxis(), false)).getSize();

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
        this.addKeyListener(new GrapherKeyListener(this));

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
    }

    private void initButtons() {
        addButton.addActionListener(this::addAction);
        clearButton.addActionListener(this::clearAction);
        resetButton.addActionListener(this::resetAction);
        rangeButton.addActionListener(this::rangeAction);
        calculatorButton.addActionListener(this::calculatorAction);
    }

    private void calculatorAction(ActionEvent event) {
        EventQueue.invokeLater(() -> new Calculator(this).setVisible(true));
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
                    String x = description.substring(
                            description.indexOf("=") + 2,
                            description.indexOf("y(t)") - 2);

                    String y = description.substring(description.lastIndexOf("=") + 2);

                    series = Compute.createParametricXYSeriesRealTimeStream(x, y, coefficientMap);

                } else {
                    series = Compute.createXYSeriesRealTimeStream(description, coefficientMap);
                }

                currentXYSeriesCollection.addSeries(series);
            } catch (IllegalArgumentException ignored) {

            }
        }
    }
}