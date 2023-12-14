package com.mathp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import static com.mathp.MathParser.FunctionHandle.*;

public class Graph extends JFrame implements Zoomable, Serializable {

    private static final long serialVersionUID = 666L;

    private final Graph instance = this;

    private static JButton additionButton = new JButton();
    private static JButton clearingButton = new JButton();
    private static JButton resettingButton = new JButton();
    private static JButton settingsButton = new JButton();
    private static JButton adjustButton = new JButton();

    private static SteppingSlider zoomingSlider = new SteppingSlider();
    private static double current_zoom;

    private static JDialog inputDialog;
    private static final InputFunctionPanel0 functionPanel = new InputFunctionPanel0();
    private static String response;

    private static final XYSeriesCollection currentXYSeriesCollection = new XYSeriesCollection();
    private static final ArrayList<String> currentXYSeriesCollectionFunctions = new ArrayList<>();
    private static final HashMap<String, String> coefficientStringMap = new HashMap<>();
    private static final HashMap<String, Double> coefficientMap = new HashMap<>();

    private static final ArrayList<Double> rangeMaxValues = new ArrayList<>();
    private static double rangeMaxValuesMinimum;

    private static final ArrayList<Double> rangeMinValues = new ArrayList<>();
    private static double rangeMinValuesMinimum;

    private static final String __FONT = "Courier New";
    private static final int __FONT_BUTTON_SIZE = 20;
    private static final int __BUTTON_HEIGHT = 96;
    private static final int __SLIDER_HEIGHT = 256;
    private static final float __STROKE_WIDTH = 2.0f;
    private static final String __IMAGE_NAME = "logo.jpg";

    private static final Integer[] ZOOM_VALUES = new Integer[]
            {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 150, 200, 250, 300, 400, 600, 900, 1200};

    private static final int ZOOM_DEFAULT_VALUE = 9;

    private static final int PRECISION_DIGITS = 3;

    private static final double DEFAULT_X = 10.0;
    private static final double DEFAULT_Y = 8.0;

    private static final double NUMBER_OF_TICKS = 10.0;
    private static final double SCALE_TO_SQUARE_FACTOR = 1.25; // 1024 / 768 is 1.33, but 1.33 is odd value, so using 5 / 4 resolution instead of 4 / 3

    private static final double ASSUMABLE_INFINITY = (double)ZOOM_VALUES[ZOOM_VALUES.length - 1] * DEFAULT_X / 100.0;

    private static final Range DEFAULT_RANGE_OF_X = new Range(-DEFAULT_X, DEFAULT_X);
    private static final Range DEFAULT_RANGE_OF_Y = new Range(-DEFAULT_Y, DEFAULT_Y);
    private static final Range COMPUTATION_RANGE = new Range(-ASSUMABLE_INFINITY, ASSUMABLE_INFINITY);

    private static double afterLowerY = -DEFAULT_Y;
    private static double afterUpperY = DEFAULT_Y;
    private static double afterLeftX = -DEFAULT_X;
    private static double afterRightX = DEFAULT_X;

    private static void resetAxes(JFreeChart chart) {
        NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
        NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();

        yAxis.centerRange(0);
        yAxis.setRange(DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(DEFAULT_RANGE_OF_Y.getUpperBound() / (NUMBER_OF_TICKS / SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(DEFAULT_RANGE_OF_X.getUpperBound() / NUMBER_OF_TICKS));

        afterLowerY = -DEFAULT_Y;
        afterUpperY = DEFAULT_Y;
        afterLeftX = -DEFAULT_X;
        afterRightX = DEFAULT_X;

        chart.getXYPlot().setRangeCrosshairValue(0);
        chart.getXYPlot().setDomainCrosshairValue(0);

        yAxis.centerRange(0);
        yAxis.setRange(DEFAULT_RANGE_OF_Y);
        yAxis.setTickUnit(new NumberTickUnit(DEFAULT_RANGE_OF_Y.getUpperBound() / (NUMBER_OF_TICKS / SCALE_TO_SQUARE_FACTOR)));

        xAxis.centerRange(0);
        xAxis.setRange(DEFAULT_RANGE_OF_X);
        xAxis.setTickUnit(new NumberTickUnit(DEFAULT_RANGE_OF_X.getUpperBound() / NUMBER_OF_TICKS));

        resetSlider(zoomingSlider);
    }

    private static boolean isInBounds(double _value) {
        return Math.abs(_value) <= ASSUMABLE_INFINITY;
    }

    private static NumberTickUnit normalNumberTickUnit(NumberAxis axis, String orientation) {
        if (orientation.equals("x"))
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / NUMBER_OF_TICKS / 2.0);
        else
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / (NUMBER_OF_TICKS / SCALE_TO_SQUARE_FACTOR) / 2.0);
    }

    private static void normalizeTick(NumberAxis axis, String orientation) {
        axis.setTickUnit(normalNumberTickUnit(axis, orientation));
    }

    private static void resetSlider(SteppingSlider slider) {
        slider.setValue(ZOOM_DEFAULT_VALUE);
    }

    private static void updateMinMax(XYSeries series) {
        rangeMaxValues.add(series.getMaxY());
        rangeMinValues.add(series.getMinY());
        rangeMinValuesMinimum = series.getMinY();
        rangeMaxValuesMinimum = series.getMaxY();

        if (isInBounds(series.getMaxY()))
            rangeMaxValuesMinimum = rangeMaxValues.stream().min(Double::compare).get();
        else
            rangeMaxValuesMinimum = ASSUMABLE_INFINITY;

        if (isInBounds(series.getMinY()))
            rangeMinValuesMinimum = rangeMinValues.stream().min(Double::compare).get();
        else
            rangeMinValuesMinimum = -ASSUMABLE_INFINITY;
    }

    private static void updateXYSeries(XYSeries series) {
        currentXYSeriesCollection.addSeries(series);
        currentXYSeriesCollectionFunctions.add(series.getDescription());
        updateMinMax(series);
    }

    private static void updatePreviousXYSeries() {
        currentXYSeriesCollection.removeAllSeries();
        for (String function: currentXYSeriesCollectionFunctions) {
            XYSeries series = createXYSeries(function);
            updateMinMax(series);
            currentXYSeriesCollection.addSeries(series);
        }
    }

    public static Font _Font(int _fontSize) {
        return new Font(__FONT, Font.PLAIN, _fontSize);
    }

    public Graph() {
        initUI();
    }

    private void initUI() {
        JFreeChart chart = createChart(currentXYSeriesCollection);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setMouseZoomable(false);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel);

        Container container = this.getContentPane();
        container.setLayout(new FlowLayout());

        Hashtable<Integer, JLabel> sliderT = new Hashtable<>();

            JLabel label25 = new JLabel("25%");
            label25.setFont(_Font(14));
            sliderT.put(25, label25);
            JLabel label100 = new JLabel("100%");
            label100.setFont(_Font(14));
            sliderT.put(100, label100);
            JLabel label400 = new JLabel("400%");
            label400.setFont(_Font(14));
            sliderT.put(400, label400);

        zoomingSlider = new SteppingSlider(ZOOM_VALUES, sliderT, ZOOM_DEFAULT_VALUE);

        zoomingSlider.setLabelTable(sliderT);
        zoomingSlider.setPaintLabels(true);
        zoomingSlider.setOrientation(JSlider.VERTICAL);
        zoomingSlider.setFocusable(false);
        zoomingSlider.setBackground(Color.WHITE);

        zoomingSlider.setForeground(Color.BLACK);
        zoomingSlider.setFont(_Font(12));

        zoomingSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                current_zoom = (double) zoomingSlider.getDomainValue() / 100;

                NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
                NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

                if (isInBounds(afterLeftX * current_zoom) && isInBounds(afterRightX * current_zoom)) {
                    xAxis.setRange(new Range(afterLeftX * current_zoom, afterRightX * current_zoom));
                    yAxis.setRange(new Range(afterLowerY * current_zoom, afterUpperY * current_zoom));
                }

                normalizeTick(xAxis, "x");
                normalizeTick(yAxis, "y");
            }
        });

        additionButton = new JButton((new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputDialog == null) {
                    inputDialog = new JDialog(instance, "Function Input", true);
                }
                inputDialog.getContentPane().add(functionPanel);
                inputDialog.pack();
                inputDialog.setLocationRelativeTo(instance);
                inputDialog.setVisible(true);

//                if (!coefficientMap.isEmpty())
//                    functionPanel.getCoefficientInputTextField().setText(MathParser.Precision._fformat(coefficientMap.values().stream().toList().get(0)));

                if (functionPanel.isFinal()) {
                    response = functionPanel.getInputFieldText();

                    functionPanel.setState(InputState.FUNCTION_SET_STATE);
                    coefficientMap.putAll(functionPanel.getMap());
                    coefficientStringMap.putAll(functionPanel.getStringMap());

                    updateXYSeries(createXYSeries(response));
                    updatePreviousXYSeries();
                }
            }
        }));

        additionButton.setFocusable(false);
        additionButton.setText("Add");
        additionButton.setFont(_Font(__FONT_BUTTON_SIZE));
        additionButton.setHorizontalTextPosition(JButton.CENTER);
        additionButton.setVerticalTextPosition(JButton.CENTER);
        additionButton.setBackground(Color.WHITE);
        //container.add(addb);
//
//        addb.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                InputFunctionPanel p = new InputFunctionPanel();
//                JFrame f = new JFrame();
//                f.add(p);
//                f.setVisible(true);
//
//                String eval = p.getInputFieldText();
//                updateXYSeries(createXYSeries(eval, -11, 10));
//            }
//        });

        //////////////////////////
        //////////////////////////

        clearingButton.setFocusable(false);
        clearingButton.setText("Clear");
        clearingButton.setFont(_Font(__FONT_BUTTON_SIZE));
        clearingButton.setHorizontalTextPosition(JButton.CENTER);
        clearingButton.setVerticalTextPosition(JButton.CENTER);
        clearingButton.setBackground(Color.WHITE);
        //container.add(rmb);

        clearingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                currentXYSeriesCollection.removeAllSeries();
                resetAxes(chart);
                resetSlider(zoomingSlider);
            }
        });

        /////////////////////////
        /////////////////////////

        resettingButton.setFocusable(false);
        resettingButton.setText("Reset");
        resettingButton.setFont(_Font(__FONT_BUTTON_SIZE));
        resettingButton.setHorizontalTextPosition(JButton.CENTER);
        resettingButton.setVerticalTextPosition(JButton.CENTER);
        resettingButton.setBackground(Color.WHITE);
        //container.add(rmb);

        resettingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                resetAxes(chart);
                resetSlider(zoomingSlider);
            }
        });

        settingsButton.setFocusable(false);
        settingsButton.setText("Settings");
        settingsButton.setFont(_Font(__FONT_BUTTON_SIZE));
        settingsButton.setHorizontalTextPosition(JButton.CENTER);
        settingsButton.setVerticalTextPosition(JButton.CENTER);
        settingsButton.setBackground(Color.WHITE);
        //container.add(rmb);

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                chartPanel.doEditChartProperties();
            }
        });

        adjustButton.setFocusable(false);
        adjustButton.setText("Adjust");
        adjustButton.setFont(_Font(__FONT_BUTTON_SIZE));
        adjustButton.setHorizontalTextPosition(JButton.CENTER);
        adjustButton.setVerticalTextPosition(JButton.CENTER);
        adjustButton.setBackground(Color.WHITE);
        //container.add(rmb);

        adjustButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (rangeMaxValues.size() != 0) {
                    NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
                    yAxis.setRange(new Range(rangeMinValuesMinimum, rangeMaxValuesMinimum));
                    afterLowerY = rangeMinValuesMinimum;
                    afterUpperY = rangeMaxValuesMinimum;
                    afterLeftX = xAxis.getLowerBound();
                    afterRightX = xAxis.getUpperBound();
                    normalizeTick(xAxis, "x");
                    normalizeTick(yAxis, "y");
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(_Font(22));

            JMenu baseMenu = new JMenu("Menu");
            baseMenu.setFont(_Font(22));

            JMenuItem infoMenu = new JMenuItem("Info");
            infoMenu.setFont(_Font(22));
            infoMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    InfoFrame0 infoFrame = new InfoFrame0();
                    infoFrame.setVisible(true);
                }
            });

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.setFont(_Font(22));
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        baseMenu.add(infoMenu);
        baseMenu.add(exitMenu);
        baseMenu.setFont(new Font("Courier New", Font.PLAIN, 22));
        menuBar.add(baseMenu);
        this.setJMenuBar(menuBar);

        JPanel HandlePanel = new JPanel();

        GroupLayout layout = new GroupLayout(HandlePanel);
        HandlePanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(additionButton, javax.swing.GroupLayout.DEFAULT_SIZE, __BUTTON_HEIGHT, Short.MAX_VALUE)
                                        .addComponent(clearingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(resettingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(adjustButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(settingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(zoomingSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(additionButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearingButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resettingButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(adjustButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(settingsButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zoomingSlider, GroupLayout.DEFAULT_SIZE, __SLIDER_HEIGHT, Short.MAX_VALUE)
                                .addContainerGap())
        );

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                double xShift = normalNumberTickUnit((NumberAxis)chart.getXYPlot().getDomainAxis(), "x").getSize();
                double yShift = normalNumberTickUnit((NumberAxis)chart.getXYPlot().getRangeAxis(), "y").getSize();

                if (key == KeyEvent.VK_RIGHT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (isInBounds(xAxis.getUpperBound() + xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() + xShift, xAxis.getUpperBound() + xShift));
                        normalizeTick(xAxis, "x");
                    }

                    afterLeftX = xAxis.getLowerBound();
                    afterRightX = xAxis.getUpperBound();
                }

                if (key == KeyEvent.VK_LEFT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (isInBounds(xAxis.getLowerBound() - xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() - xShift, xAxis.getUpperBound() - xShift));
                        normalizeTick(xAxis, "x");
                    }

                    afterLeftX = xAxis.getLowerBound();
                    afterRightX = xAxis.getUpperBound();
                }

                if (key == KeyEvent.VK_UP) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() + yShift, yAxis.getUpperBound() + yShift));
                    normalizeTick(yAxis, "y");

                    afterLowerY = yAxis.getLowerBound();
                    afterUpperY = yAxis.getUpperBound();
                }

                if (key == KeyEvent.VK_DOWN) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() - yShift, yAxis.getUpperBound() - yShift));
                    normalizeTick(yAxis, "y");

                    afterLowerY = yAxis.getLowerBound();
                    afterUpperY = yAxis.getUpperBound();
                }

                if (key == KeyEvent.VK_MINUS) {
                    zoomingSlider.setValue(zoomingSlider.getValue() - 1);
                }

                if (key == KeyEvent.VK_EQUALS) {
                    zoomingSlider.setValue(zoomingSlider.getValue() + 1);
                }

                if (key == KeyEvent.VK_DELETE)
                    clearingButton.doClick();

                if (key == KeyEvent.VK_ESCAPE) {
                    exitMenu.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        this.add(HandlePanel);
        this.setAlwaysOnTop(true);

        pack();
        setTitle("Grapher");
        setIconImage(new ImageIcon(__IMAGE_NAME).getImage());
        setLocationRelativeTo(null);
        setResizable(false);
        setPreferredSize(new Dimension(1600, 900));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static XYSeries createXYSeries(String function) {

        XYSeries series = new XYSeries(function);
        series.setDescription(function);

        double[][] data = {};
        double[] x_vals = {};
        double[] y_vals = {};

        try {
            data = Main.getOptimizedData(function, coefficientMap, (int)PRECISION_DIGITS);
        } catch (MathParser.SyntaxParseException e) {
            e.printStackTrace();
        }

        try {
            x_vals = data[0];
            y_vals = data[1];
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < x_vals.length; i++) {
            series.add(x_vals[i], y_vals[i]);
        }

        return series;
    }

    private static XYSeriesCollection createXYSeriesCollection(XYSeries series) {

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);

        return collection;
    }

    private static JFreeChart createChart(XYSeriesCollection collection) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "x",
                "y",
                collection,
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

        NumberAxis xAxis = (NumberAxis)plot.getDomainAxis();
        xAxis.setRange(-DEFAULT_X, DEFAULT_X);
        xAxis.setAutoTickUnitSelection(true);
        xAxis.setTickUnit(new NumberTickUnit(DEFAULT_X / NUMBER_OF_TICKS));

        NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
        yAxis.setRange(-DEFAULT_Y, DEFAULT_Y);
        yAxis.setAutoTickUnitSelection(true);
        yAxis.setTickUnit(new NumberTickUnit(DEFAULT_Y / (NUMBER_OF_TICKS / SCALE_TO_SQUARE_FACTOR)));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

            renderer.setSeriesPaint(0, new Color((int)(Math.random() * 0x1000000)));
            renderer.setSeriesStroke(0, new BasicStroke(__STROKE_WIDTH));
            renderer.setSeriesShapesVisible(0, false);

        return chart;
    }

    public static Range getComputationRange() {
        return COMPUTATION_RANGE;
    }

    public static HashMap<String, Double> getCoefficientMap() {
        return coefficientMap;
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            Graph graph = new Graph();
            graph.setVisible(true);
        });
    }

    private static void _DEBUG() {
        System.out.println(currentXYSeriesCollection);
    }

    private static void _DEBUG2() {
        System.out.println(java.util.Arrays.toString(currentXYSeriesCollection.getSeries().toArray()));
    }

    @Override
    public boolean isDomainZoomable() {
        return true;
    }

    @Override
    public boolean isRangeZoomable() {
        return true;
    }

    @Override
    public PlotOrientation getOrientation() {
        return null;
    }

    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {

    }

    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {

    }

    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {

    }

    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {

    }

    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {

    }

    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {

    }
}