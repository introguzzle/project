package ru.grapher;

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
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.DoubleStream;

public class Graph extends JFrame implements Zoomable, Serializable {

    @Serial
    private static final long serialVersionUID      = 666L;

    private static final int PRECISION_DIGITS       = 2;
    private static final Mode MODE                  = Mode.HALF;

    private static final boolean ASK_CONFIRMATION   = true;

    private final Graph instance                    = this;

    private static final Logger logger              = Logger.getLogger("ru.grapher");

    private static JButton additionButton           = new JButton();
    private static final JButton clearingButton     = new JButton();
    private static final JButton resettingButton    = new JButton();
    private static final JButton settingsButton     = new JButton();
    private static JButton rangeButton        = new JButton();
    private static final JButton adjustButton       = new JButton();

    private static final boolean evenAdjust         = false;

    private static JComboBox<String> coefficientBox = new JComboBox<>();

    private static SteppingSlider<Double> coefficientSlider = new SteppingSlider<>();
    private static SteppingSlider<Integer> zoomingSlider    = new SteppingSlider<>();

    private static boolean sliderSwitcher           = true;
    private static boolean handlePanelSwitcher      = true;

    private static double currentZoom               = 1.0;
    private static String currentCoefficient        = null;
    private static final double DEFAULT_MAXIMUM     = 10.0;
    private static final double DEFAULT_MINIMUM     = -13.0;
    private static final double DEFAULT_STEP        = 0.1;

    private static String[] coefficientArray                    = new String[]{};
    private static final HashMap<String, Double> coefficientMap = new HashMap<>();

    private static int additionButtonInvokeCount    = 0;

    private static JDialog rangeDialog;
    private static final RangePanel rangePanel = new RangePanel(
            DEFAULT_MAXIMUM,
            DEFAULT_MINIMUM,
            DEFAULT_STEP
    );

    private static JDialog inputDialog;
    private static final InputFunctionPanel functionPanel       = new InputFunctionPanel();
    private static String response;

    private static final XYSeriesCollection currentXYSeriesCollection                       = new XYSeriesCollection();
    private static final ConcurrentHashMap<XYSeries, ArrayList<String>> currentXYSeriesMap  = new ConcurrentHashMap<>();

    private static final ArrayList<Double> rangeMaxValues       = new ArrayList<>();
    private static double rangeMaxValuesMinimum;

    private static final ArrayList<Double> rangeMinValues       = new ArrayList<>();
    private static double rangeMinValuesMinimum;

    private static final double COEFFICIENT_SLIDER_VALUE_MULTIPLIER = 1.0;
    private static final double COEFFICIENT_SLIDER_DIVISIONS        = 8.0;

    private static final double NUMBER_OF_TICKS                 = 10.0;
    private static final double SCALE_TO_SQUARE_FACTOR          = 1.25; // 1024 / 768 is 1.33, but 1.33 is odd value, so using 5 / 4 resolution instead of 4 / 3

    private static final double DEFAULT_X                       = 10.0;
    private static final double DEFAULT_Y                       = 8.0;
    private static final Range DEFAULT_RANGE_OF_X               = new Range(-DEFAULT_X, DEFAULT_X);
    private static final Range DEFAULT_RANGE_OF_Y               = new Range(-DEFAULT_Y, DEFAULT_Y);

    private static double afterLowerY                           = -DEFAULT_Y;
    private static double afterUpperY                           = DEFAULT_Y;
    private static double afterLowerX                           = -DEFAULT_X;
    private static double afterUpperX                           = DEFAULT_X;

    private static void reinitXYSeriesCollection() {

        currentXYSeriesCollection.removeAllSeries();

        for (Map.Entry<XYSeries, ArrayList<String>> entry: currentXYSeriesMap.entrySet()) {
            if (entry.getValue().contains(currentCoefficient)) {
                currentXYSeriesCollection.addSeries(createXYSeriesRealTime(entry.getKey().getDescription()));
            } else {
                currentXYSeriesCollection.addSeries(entry.getKey());
            }
        }
    }

    private static void addToXYSeriesCollection(final XYSeries series) {
        ArrayList<String> coefficients = MathParser.FunctionHandler.getCoefficients(series.getDescription());

        if (Collections.indexOfSubList(new ArrayList<>(Arrays.asList(coefficientArray)), coefficients) != -1) {
            currentXYSeriesCollection.addSeries(series);
            currentXYSeriesMap.put(series, coefficients);

            if (evenAdjust)
                updateMinMax(series);
        }
    }

    private static void updateXYSeriesCollection() {

        currentXYSeriesCollection.removeAllSeries();

        for (Map.Entry<XYSeries, ArrayList<String>> entry: currentXYSeriesMap.entrySet()) {
            currentXYSeriesCollection.addSeries(createXYSeriesRealTime(entry.getKey().getDescription()));
        }

    }

    public Graph() {
        initUI();
    }

    private void initUI() {
        JFreeChart chart = createChart();

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

        coefficientSlider = getDoubleCoefficientSlider(
                DEFAULT_MINIMUM,
                DEFAULT_MAXIMUM,
                DEFAULT_STEP,
                COEFFICIENT_SLIDER_DIVISIONS,
                true
        );

        coefficientSlider.setEnabled(false);
        coefficientSlider.setPaintLabels(true);
        coefficientSlider.setOrientation(JSlider.VERTICAL);
        coefficientSlider.setFocusable(false);
        coefficientSlider.setBackground(Color.WHITE);
        coefficientSlider.setPreferredSize(new Dimension(20, __SLIDER_HEIGHT));
        coefficientSlider.setForeground(Color.BLACK);
        coefficientSlider.setFont(getDefaultFont(12));

        coefficientSlider.addMouseListener(coefficientSliderMouseListener());
        coefficientSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!sliderSwitcher && coefficientSlider.isEnabled()) {
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(currentCoefficient, coefficientSlider.getDomainValue() * COEFFICIENT_SLIDER_VALUE_MULTIPLIER);
                    updateXYSeriesCollection();
                }
            }
        });

        zoomingSlider = new SteppingSlider<>(ZOOM_VALUES, getZoomTable(), ZOOM_DEFAULT_INDEX, true);

        zoomingSlider.setPaintLabels(true);
        zoomingSlider.setOrientation(JSlider.VERTICAL);
        zoomingSlider.setFocusable(false);
        zoomingSlider.setBackground(Color.WHITE);
        zoomingSlider.setThumbColor(Color.RED);
        zoomingSlider.setPreferredSize(new Dimension(20, __SLIDER_HEIGHT));
        zoomingSlider.setForeground(Color.BLACK);
        zoomingSlider.setFont(getDefaultFont(12));

        zoomingSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentZoom = (double)zoomingSlider.getDomainValue() / 100;

                NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
                NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

                if (isInBounds(afterLowerX * currentZoom)
                        && isInBounds(afterUpperX * currentZoom)) {

                    xAxis.setRange(
                            new Range(afterLowerX * currentZoom, afterUpperX * currentZoom)
                    );

                    yAxis.setRange(
                            new Range(afterLowerY * currentZoom, afterUpperY * currentZoom)
                    );
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
                    inputDialog.setResizable(false);
                }

                additionButtonInvokeCount++;

                inputDialog.getContentPane().add(functionPanel);
                inputDialog.pack();

                inputDialog.setLocationRelativeTo(instance);
                inputDialog.setVisible(true);
                inputDialog.setResizable(false);

                if (functionPanel.isFinal()) {
                    response = functionPanel.getInputFieldText();

                    functionPanel.setState(InputState.FUNCTION_SET_STATE);
                    coefficientMap.putAll(functionPanel.getMap());
                    coefficientArray = coefficientMap.keySet().toArray(new String[0]);

                    if (additionButtonInvokeCount > 0 && !coefficientMap.isEmpty()) {
                        coefficientBox.setEnabled(true);
                        coefficientBox.setModel(new DefaultComboBoxModel<>(coefficientArray));

                        coefficientSlider.setEnabled(true);
                        double value = coefficientMap.get((String) coefficientBox.getSelectedItem());

                        coefficientSlider.setClosestDomainValue(value);
                    }

                    if (MathParser.FunctionHandler.getCoefficients(response).isEmpty()) {
                        addToXYSeriesCollection(createXYSeriesRealTime(response));

                    } else {
                        addToXYSeriesCollection(createXYSeriesRealTime(response));
                        updateXYSeriesCollection();
                    }
                }
            }
        }));

        additionButton.setFocusable(false);
        additionButton.setText("Add");
        additionButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        additionButton.setHorizontalTextPosition(JButton.CENTER);
        additionButton.setVerticalTextPosition(JButton.CENTER);
        additionButton.setBackground(Color.WHITE);

        clearingButton.setFocusable(false);
        clearingButton.setText("Clear");
        clearingButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        clearingButton.setHorizontalTextPosition(JButton.CENTER);
        clearingButton.setVerticalTextPosition(JButton.CENTER);
        clearingButton.setBackground(Color.WHITE);

        clearingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                currentXYSeriesCollection.removeAllSeries();
                currentXYSeriesMap.clear();

                resetAxes(chart);
                resetSlider(zoomingSlider);

                coefficientBox.setEnabled(false);
                coefficientBox.setModel(InputFunctionPanel.getDefaultComboBoxModel());

                coefficientSlider.setEnabled(false);
            }
        });

        resettingButton.setFocusable(false);
        resettingButton.setText("Reset");
        resettingButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        resettingButton.setHorizontalTextPosition(JButton.CENTER);
        resettingButton.setVerticalTextPosition(JButton.CENTER);
        resettingButton.setBackground(Color.WHITE);

        resettingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                resetAxes(chart);
                resetSlider(zoomingSlider);
            }
        });

        rangeButton = new JButton((new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JFrame instance = (Graph) SwingUtilities.getWindowAncestor((JButton) evt.getSource());

                if (rangeDialog == null) {
                    rangeDialog = new JDialog(
                            instance,
                            "Set Range",
                            true
                    );

                    rangeDialog.setIconImage(__IMAGE);
                    rangeDialog.setResizable(false);
                }

                rangeDialog.getContentPane().add(rangePanel);
                rangeDialog.pack();

                rangeDialog.setLocationRelativeTo(instance);
                rangeDialog.setVisible(true);
                rangeDialog.setIconImage(__IMAGE);
                rangeDialog.setResizable(false);

                if (rangePanel.isFinal()) {

                    double min = Double.parseDouble(rangePanel.getMinimalValueField().getText());
                    double max = Double.parseDouble(rangePanel.getMaximalValueField().getText());
                    double step = Double.parseDouble(rangePanel.getStepField().getText());

                    if (max > min) {
                        ArrayList<Double> values = getDoubleCoefficientValues(min, max, step, COEFFICIENT_SLIDER_DIVISIONS);

                        Hashtable<Integer, JLabel> labels = getDoubleCoefficientSliderSimpleLabels(min, max, step, COEFFICIENT_SLIDER_DIVISIONS);

                        coefficientSlider.setConfiguration(
                                values,
                                coefficientSlider.getValue(),
                                labels
                        );
                    }

                    rangePanel.setFinal(true);
                }
            }
        }));

        rangeButton.setFocusable(false);
        rangeButton.setText("Range");
        rangeButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        rangeButton.setHorizontalTextPosition(JButton.CENTER);
        rangeButton.setVerticalTextPosition(JButton.CENTER);
        rangeButton.setBackground(Color.WHITE);

        adjustButton.setFocusable(false);
        adjustButton.setText("Adjust");
        adjustButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        adjustButton.setHorizontalTextPosition(JButton.CENTER);
        adjustButton.setVerticalTextPosition(JButton.CENTER);
        adjustButton.setBackground(Color.WHITE);

        adjustButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (!rangeMaxValues.isEmpty()) {
                    NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(rangeMinValuesMinimum, rangeMaxValuesMinimum));

                    afterLowerY = rangeMinValuesMinimum;
                    afterUpperY = rangeMaxValuesMinimum;

                    afterLowerX = xAxis.getLowerBound();
                    afterUpperX = xAxis.getUpperBound();

                    normalizeTick(xAxis, "x");
                    normalizeTick(yAxis, "y");
                }
            }
        });

        settingsButton.setFocusable(false);
        settingsButton.setText("Settings");
        settingsButton.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        settingsButton.setHorizontalTextPosition(JButton.CENTER);
        settingsButton.setVerticalTextPosition(JButton.CENTER);
        settingsButton.setBackground(Color.WHITE);

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                chartPanel.doEditChartProperties();
            }
        });

        coefficientBox.setFocusable(false);
        coefficientBox.setEnabled(false);
        coefficientBox.setFont(getDefaultFont(__FONT_BUTTON_SIZE));
        coefficientBox.setBackground(Color.WHITE);

        coefficientBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                double value = coefficientMap.get((String) coefficientBox.getSelectedItem());
                coefficientSlider.setDomainValue(value);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(getDefaultFont(22));

        JMenu menu = new JMenu("Menu");
        menu.setFont(getDefaultFont(22));

        JMenuItem infoMenu = getInfoMenuItem();
        JMenuItem exitMenu = getExitMenuItem();

        menu.add(infoMenu);
        menu.add(exitMenu);

        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        JPanel handlePanel = new JPanel();
        GroupLayout layout = new GroupLayout(handlePanel);
        handlePanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(additionButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(clearingButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(resettingButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rangeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(settingsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(coefficientBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(coefficientSlider, GroupLayout.PREFERRED_SIZE, __MAGIC_WIDTH, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(zoomingSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                .addComponent(rangeButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(settingsButton, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(coefficientBox, GroupLayout.PREFERRED_SIZE, __BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(coefficientSlider, GroupLayout.DEFAULT_SIZE, __SLIDER_HEIGHT, Short.MAX_VALUE)
                                        .addComponent(zoomingSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        if (ASK_CONFIRMATION) {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent evt) {
                    JDialog exitDialog = new JDialog((Graph) evt.getSource(),
                            __NAME, true);
                    exitDialog.add(new ExitPanel(true));

                    exitDialog.pack();

                    JFrame instance = (Graph) evt.getSource();
                    instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                    exitDialog.setIconImage(__IMAGE);
                    exitDialog.setResizable(false);
                    exitDialog.setAlwaysOnTop(true);
                    exitDialog.setLocationRelativeTo(null);
                    exitDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                    exitDialog.setVisible(true);
                }
            });
        }

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                final int KEY = e.getKeyCode();

                double xShift = getNormalNumberTickUnit(
                        (NumberAxis)chart.getXYPlot().
                                getDomainAxis(), "x").getSize();

                double yShift = getNormalNumberTickUnit(
                        (NumberAxis)chart.getXYPlot().
                                getRangeAxis(), "y").getSize();

                if (KEY == KeyEvent.VK_RIGHT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (isInBounds(xAxis.getUpperBound() + xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() + xShift, xAxis.getUpperBound() + xShift));
                        normalizeTick(xAxis, "x");
                    }

                    afterLowerX = xAxis.getLowerBound();
                    afterUpperX = xAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_LEFT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (isInBounds(xAxis.getLowerBound() - xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() - xShift, xAxis.getUpperBound() - xShift));
                        normalizeTick(xAxis, "x");
                    }

                    afterLowerX = xAxis.getLowerBound();
                    afterUpperX = xAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_UP) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() + yShift, yAxis.getUpperBound() + yShift));
                    normalizeTick(yAxis, "y");

                    afterLowerY = yAxis.getLowerBound();
                    afterUpperY = yAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_DOWN) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() - yShift, yAxis.getUpperBound() - yShift));
                    normalizeTick(yAxis, "y");

                    afterLowerY = yAxis.getLowerBound();
                    afterUpperY = yAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_0) {
                    if (sliderSwitcher) {
                        sliderSwitcher = false;
                        coefficientSlider.setThumbColor(Color.RED);
                        zoomingSlider.setThumbColor(Color.BLACK);

                    } else {
                        sliderSwitcher = true;
                        coefficientSlider.setThumbColor(Color.BLACK);
                        zoomingSlider.setThumbColor(Color.RED);
                    }
                }

                if (KEY == KeyEvent.VK_MINUS) {
                    if (sliderSwitcher)
                        zoomingSlider.setValue(zoomingSlider.getValue() - 1);
                }

                if (KEY == KeyEvent.VK_EQUALS) {
                    if (sliderSwitcher)
                        zoomingSlider.setValue(zoomingSlider.getValue() + 1);
                }

                if (KEY == KeyEvent.VK_R) {
                    resettingButton.doClick();
                    resettingButton.doClick();
                    resettingButton.doClick();
                }

                if (KEY == KeyEvent.VK_DELETE) {
                    clearingButton.doClick();
                    clearingButton.doClick();
                    clearingButton.doClick();
                }

                if (KEY == KeyEvent.VK_ESCAPE) {
                    getExitMenuItem().doClick();
                }

                if (KEY == KeyEvent.VK_1) {
                    if (handlePanelSwitcher) {
                        handlePanelSwitcher = false;
                        handlePanel.setVisible(false);
                    } else {
                        handlePanelSwitcher = true;
                        handlePanel.setVisible(true);
                    }
                }

                if (KEY == KeyEvent.VK_2) {
                    chartPanel.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    additionButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    settingsButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    adjustButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    resettingButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    clearingButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    zoomingSlider.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    coefficientSlider.setBackground(new Color((int)(Math.random() * 0x1000000)));
                }

                if (KEY == KeyEvent.VK_3) {
                    int r = (int)(Math.random() * 255);
                    int g = (int)(Math.random() * 255);
                    int b = (int)(Math.random() * 255);

                    Color style1 = new Color(r, g, b);

                    int max = Math.max(r, Math.max(g, b));
                    int greyOffset = 12;

//                    int min = Math.min(r, Math.min(g, b));
//                    int avg = (r + g + b) / 3;
//                    int offset = 30;

//                    int tone = (int)(0.33 * r + 0.5 * g + 0.16 * b);
//
//                    int tone = 255 - max > 80 && 255 - max < 160 ? 255 - max : avg - min;

                    int tone = max > 255 / 2 ? greyOffset : 255 - greyOffset;

                    System.out.println("tone = " + tone);
                    System.out.println("last color, r = " + r + " g = " + g + " b = " + b);

                    Color style2 = new Color(tone, tone, tone);

                    chartPanel.setBackground(style1);
                    chartPanel.setForeground(style2);

                    additionButton.setBackground(style1);
                    additionButton.setForeground(style2);

                    settingsButton.setBackground(style1);
                    settingsButton.setForeground(style2);

                    adjustButton.setBackground(style1);
                    adjustButton.setForeground(style2);

                    resettingButton.setBackground(style1);
                    resettingButton.setForeground(style2);

                    clearingButton.setBackground(style1);
                    clearingButton.setForeground(style2);

                    coefficientBox.setBackground(style1);
                    coefficientBox.setForeground(style2);

                    zoomingSlider.setBackground(style1);
                    zoomingSlider.setForeground(style2);


                    coefficientSlider.setBackground(style1);
                    coefficientSlider.setForeground(style2);
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                final int KEY = evt.getKeyCode();

                if (KEY == KeyEvent.VK_MINUS && !sliderSwitcher && coefficientSlider.isEnabled()) {
                    coefficientSlider.setValue(coefficientSlider.getValue() - 1);
                }

                if (KEY == KeyEvent.VK_EQUALS && !sliderSwitcher && coefficientSlider.isEnabled()) {
                    coefficientSlider.setValue(coefficientSlider.getValue() + 1);
                }
            }
        });

        this.add(handlePanel);
        this.setAlwaysOnTop(true);

        this.pack();

        this.setTitle(__NAME);
        this.setIconImage(__IMAGE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setPreferredSize(DIMENSION);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static JMenuItem getInfoMenuItem() {
        JMenuItem infoMenu = new JMenuItem("Info");

        infoMenu.setFont(getDefaultFont(22));
        infoMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                InfoFrame infoFrame = new InfoFrame();
                infoFrame.setVisible(true);
            }
        });

        return infoMenu;
    }

    private static JMenuItem getExitMenuItem() {

        JMenuItem exitMenu = new JMenuItem("Exit");

        exitMenu.setFont(getDefaultFont(22));
        if (ASK_CONFIRMATION) {
            exitMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JDialog exitDialog = new JDialog((JFrame) ((JMenuItem) evt.getSource()).getParent().getParent(),
                            __NAME, true);
                    exitDialog.add(new ExitPanel(false));

                    exitDialog.pack();

                    exitDialog.setIconImage(__IMAGE);
                    exitDialog.setResizable(false);
                    exitDialog.setAlwaysOnTop(true);
                    exitDialog.setLocationRelativeTo(null);

                    exitDialog.setVisible(true);
                }
            });
        }

        return exitMenu;
    }

    private static Hashtable<Integer, JLabel> getCoefficientTable() {
        Hashtable<Integer, JLabel> coefficientTable = new Hashtable<>();

        JLabel _maxValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_VALUES.size() - 1).toString());
        _maxValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        coefficientTable.put(COEFFICIENT_SLIDER_VALUES.size() - 1, _maxValue);

        JLabel _upperQuarterValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX).toString());
        _upperQuarterValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        coefficientTable.put(COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX, _upperQuarterValue);

        JLabel _defaultValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_DEFAULT_INDEX).toString());
        _defaultValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        coefficientTable.put(COEFFICIENT_SLIDER_DEFAULT_INDEX, _defaultValue);

        JLabel _lowerQuarterValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX).toString());
        _lowerQuarterValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        coefficientTable.put(COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX, _lowerQuarterValue);

        JLabel _minValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(0).toString());
        _minValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        coefficientTable.put(0, _minValue);
        return coefficientTable;
    }

    private static Hashtable<Integer, JLabel> getZoomTable() {
        Hashtable<Integer, JLabel> zoomTable = new Hashtable<>();

        JLabel maxValue = new JLabel(ZOOM_VALUES.get(ZOOM_VALUES.size() - 1).toString() + "%");
        maxValue.setFont(getDefaultFont(__ZOOM_SLIDER_FONT_SIZE));
        zoomTable.put(ZOOM_VALUES.size() - 1, maxValue);

        JLabel upperQuarterValue = new JLabel(ZOOM_VALUES.get(ZOOM_UPPER_QUARTER_INDEX).toString() + "%");
        upperQuarterValue.setFont(getDefaultFont(__ZOOM_SLIDER_FONT_SIZE));
        zoomTable.put(ZOOM_UPPER_QUARTER_INDEX, upperQuarterValue);

        JLabel defaultValue = new JLabel(ZOOM_VALUES.get(ZOOM_VALUES.size() / 2).toString() + "%");
        defaultValue.setFont(getDefaultFont(__ZOOM_SLIDER_FONT_SIZE));
        zoomTable.put(ZOOM_VALUES.size() / 2, defaultValue);

        JLabel lowerQuarterValue = new JLabel(ZOOM_VALUES.get(ZOOM_LOWER_QUARTER_INDEX).toString() + "%");
        lowerQuarterValue.setFont(getDefaultFont(__ZOOM_SLIDER_FONT_SIZE));
        zoomTable.put(ZOOM_LOWER_QUARTER_INDEX, lowerQuarterValue);

        JLabel minValue = new JLabel(ZOOM_VALUES.get(0).toString() + "%");
        minValue.setFont(getDefaultFont(__ZOOM_SLIDER_FONT_SIZE));
        zoomTable.put(0, minValue);
        return zoomTable;
    }

    private static boolean containsAnyIn(final List<String> list) {
        for (var s : list) {
            if (Graph.coefficientMap.containsKey(s))
                return true;
        }
        return false;
    }

    private static double compute(final String expression, final double value) throws MathParser.SyntaxParseException {

        String changed = expression;

        changed = MathParser.FunctionHandler.replaceVariable(changed, value);
        changed = MathParser.FunctionHandler.replaceConstants(changed);

        List<String> coefficientsOfThis = MathParser.FunctionHandler.getCoefficients(changed);

        if (!Graph.coefficientMap.isEmpty()) {
            for (Map.Entry<String, Double> entry : Graph.coefficientMap.entrySet()) {
                String coefficient = entry.getKey();
                double v = entry.getValue();
                if (containsAnyIn(coefficientsOfThis)) {
                    changed = MathParser.FunctionHandler.replaceCoefficient(changed, coefficient, v);
                }
            }
        }

        return MathParser.parse(changed);
    }

    private static final XYSeries createXYSeriesRealTimeStream(final String function) {
        final int THREADS_COUNT = getThreads();
        XYSeries series = new XYSeries(function, true, true);
        series.setDescription(function);

        DoubleStream data = DoubleStream.iterate(-ASSUMABLE_INFINITY, d -> d < ASSUMABLE_INFINITY, d -> d + 1 / Math.pow(10, PRECISION_DIGITS));

        data.parallel().forEach(x -> {
            try {
                series.add(x, compute(function, x));
            } catch (MathParser.SyntaxParseException e) {
                logger.log(Level.SEVERE, "parsing error", e);
            }
        });

        return series;
    }

    private static final XYSeries createXYSeriesRealTime(final String function) {
        final int THREADS_COUNT = getThreads();
        XYSeries series = new XYSeries(function, true, true);
        series.setDescription(function);

        ArrayList<Runnable> tasks = new ArrayList<>();

        ExecutorService pool = null;

        try {
            pool = Executors.newFixedThreadPool(THREADS_COUNT);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "illegal threads", e);
        }

        final double delta = COMPUTATION_RANGE.getLength() / THREADS_COUNT;

        final double step = 1 / Math.pow(10, PRECISION_DIGITS);

        for (double p = -ASSUMABLE_INFINITY; p <= ASSUMABLE_INFINITY; p = p + delta) {
            final double fp = p;
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    for (double x = fp; x < fp + delta; x = x + step) {
                        try {
                            series.add(x, compute(function, x));
                        } catch (MathParser.SyntaxParseException e) {
                            logger.log(Level.SEVERE, "parsing error", e);
                        }
                    }
                }
            });
        }

        for (Runnable task: tasks) {
            Objects.requireNonNull(pool).submit(task);
        }

        return series;
    }

    private static final XYSeries createXYSeriesComplete(final String function) {
        final int THREADS_COUNT = getThreads();
        XYSeries series = new XYSeries(function);
        series.setDescription(function);

        ExecutorService pool = Executors.newFixedThreadPool(THREADS_COUNT);

        ExecutorCompletionService<Double> completionService = new ExecutorCompletionService<>(pool);

        final ArrayList<Runnable> tasks = new ArrayList<>();
        final List<Future<Double>> futures = new ArrayList<>();

        final double delta = COMPUTATION_RANGE.getLength() / THREADS_COUNT;
        final double left = COMPUTATION_RANGE.getLowerBound();
        final double right = COMPUTATION_RANGE.getUpperBound();

        final double step = 1 / Math.pow(10, PRECISION_DIGITS);

        for (double p = left; p <= right; p = p + delta) {
            final double fp = p;
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    for (double x = fp; x < fp + delta; x = x + step) {
                        try {

                            series.add(x, compute(function, x));

                        } catch (MathParser.SyntaxParseException e) {
                            logger.log(Level.WARNING, "parsing error", e);
                        }
                    }
                }
            });
        }

        for (Runnable task : tasks) {
            Future f = pool.submit(task);
            futures.add(f);
        }

        for (Future<?> f: futures) {
            try {
                System.out.println("future.isDone = " + f.isDone());
                System.out.println("future: call = "+ f.get());
                f.get();
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "interrupted exception", e);
            } catch (ExecutionException e) {
                logger.log(Level.WARNING, "execution exception", e);
            }
        }

        return series;
    }

    private static int getThreads(final int threads) {
        if (MODE == Mode.ALL)
            return Runtime.getRuntime().availableProcessors();
        else if (MODE == Mode.HALF)
            return Runtime.getRuntime().availableProcessors() / 2;
        else if (MODE == Mode.SINGLE)
            return 1;
        else if (MODE == Mode.CUSTOM && threads < Runtime.getRuntime().availableProcessors())
            return threads;

        return 1;
    }

    public static int getThreads() {
        if (MODE == Mode.ALL)
            return Runtime.getRuntime().availableProcessors();
        else if (MODE == Mode.HALF)
            return Runtime.getRuntime().availableProcessors() / 2;
        else if (MODE == Mode.SINGLE)
            return 1;
        else
            return 1;
    }

    private static JFreeChart createChart() {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "x",
                "y",
                currentXYSeriesCollection,
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

        renderer.setSeriesPaint(0, new Color(
                (int)(Math.random() * 100),
                (int)(Math.random() * 100),
                (int)(Math.random() * 100))
        );
        renderer.setSeriesStroke(0, new BasicStroke(__STROKE_WIDTH));
        renderer.setSeriesShapesVisible(0, false);

        return chart;
    }

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
        afterLowerX = -DEFAULT_X;
        afterUpperX = DEFAULT_X;

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

    private static MouseListener coefficientSliderMouseListener() {

        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (coefficientSlider.isEnabled()) {
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(currentCoefficient, coefficientSlider.getDomainValue() * COEFFICIENT_SLIDER_VALUE_MULTIPLIER);
                    updateXYSeriesCollection();
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {

            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (coefficientSlider.isEnabled()) {
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(currentCoefficient, coefficientSlider.getDomainValue() * COEFFICIENT_SLIDER_VALUE_MULTIPLIER);
                    updateXYSeriesCollection();
                }
            }

            @Override
            public void mouseEntered(MouseEvent evt) {

            }

            @Override
            public void mouseExited(MouseEvent evt) {

            }
        };
    }

    public static Font getDefaultFont(final int _fontSize) {
        return new Font(__FONT, Font.PLAIN, _fontSize);
    }

    private static boolean isInBounds(final double _value) {
        return Math.abs(_value) <= ASSUMABLE_INFINITY;
    }

    private static NumberTickUnit getNormalNumberTickUnit(final NumberAxis axis,
                                                          final String orientation) {
        if (orientation.equals("x"))
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / NUMBER_OF_TICKS / 2.0);
        else if (orientation.equals("y"))
            return new NumberTickUnit(Math.abs(axis.getUpperBound() - axis.getLowerBound()) / (NUMBER_OF_TICKS / SCALE_TO_SQUARE_FACTOR) / 2.0);
        else
            return null;
    }

    private static void normalizeTick(NumberAxis axis,
                                      final String orientation) {
        axis.setTickUnit(getNormalNumberTickUnit(axis, orientation));
    }

    private static void resetSlider(SteppingSlider<Integer> slider) {
        slider.setValue(ZOOM_DEFAULT_INDEX);
    }

    private static void updateMinMax(final XYSeries series) {
        rangeMaxValues.add(series.getMaxY());
        rangeMinValues.add(series.getMinY());
        rangeMinValuesMinimum = series.getMinY();
        rangeMaxValuesMinimum = series.getMaxY();

        if (isInBounds(series.getMaxY()))
            rangeMaxValuesMinimum = rangeMaxValues.stream().min(Double::compare).orElse(1.0);
        else
            rangeMaxValuesMinimum = ASSUMABLE_INFINITY;

        if (isInBounds(series.getMinY()))
            rangeMinValuesMinimum = rangeMinValues.stream().min(Double::compare).orElse(-1.0);
        else
            rangeMinValuesMinimum = -ASSUMABLE_INFINITY;
    }

    private static SteppingSlider<Integer> getCoefficientSlider() {
        return new SteppingSlider<>(COEFFICIENT_SLIDER_VALUES, getCoefficientTable(), COEFFICIENT_SLIDER_DEFAULT_INDEX);
    }

    private static ArrayList<Double> getDoubleCoefficientValues(final double min,
                                                                final double max,
                                                                final double step,
                                                                final double divisions) {
        ArrayList<Double> values = new ArrayList<>();


        int before = (int)((Math.abs(min - max)) / step);
        int dividable = before;

        if (dividable % divisions != 0)
            for (;; dividable++) {
                if (dividable % divisions == 0) {
                    break;
                }
            }

        double fmax = max + step * (dividable - before);
        double digits = Math.pow(10, BigDecimal.valueOf(step).scale());

        for (double d = min; d < fmax; d += step)
            values.add(Math.round(d * digits) / digits);

        return values;
    }

    private static JLabel getDoubleCoefficientSliderLabel(final double value) {
        JLabel label = new JLabel(String.format("%.2f", value));
        label.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        return label;
    }

    private static Hashtable<Integer, JLabel> getDoubleCoefficientSliderSimpleLabels(final double min,
                                                                                     final double max,
                                                                                     final double step,
                                                                                     final double divisions) {

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        ArrayList<Double> values = getDoubleCoefficientValues(min, max, step, divisions);

        int size = values.size();


        for (int index = size - 1; index >= 0; index -= size / (int)divisions) {
            JLabel label = getDoubleCoefficientSliderLabel(values.get(index));
            labels.put(index, label);
        }

        labels.put(0, getDoubleCoefficientSliderLabel(min));

        return labels;
    }

    private static Hashtable<Integer, JLabel> getDoubleCoefficientSliderLabels(final double min,
                                                                               final double max,
                                                                               final double step,
                                                                               final double divisions) {
        Hashtable<Integer, JLabel> labels = new Hashtable<>();

        ArrayList<Double> values = getDoubleCoefficientValues(min, max, step, divisions);

        int pindex = -1;

        int zindex = -1;
        int prev = -1;
        int next = -1;

        double fmax = values.get(values.size() - 1);

        boolean piFindable = fmax > 3.15;
        boolean zeroFindable = fmax > -0.01;

        boolean piPlaceable = divisions <= 4;
        boolean zeroPlaceable = divisions <= 4;

        int size = values.size();

        for (int i = 0; i < values.size(); i++) {
            if (piFindable)
                if (Math.abs(values.get(i) - 3.14) < step) {
                    pindex = i;
                }

            if (zeroFindable)
                if (Math.abs(values.get(i)) < step) {
                    zindex = i;
                }
        }

        for (int index = size - 1; index >= 0; index -= size / (int)divisions) {
            JLabel label = getDoubleCoefficientSliderLabel(values.get(index));

            zeroPlaceable = zeroPlaceable && !label.getText().equals("0.0");

            labels.put(index, label);
        }

        ArrayList<Integer> indices = new ArrayList<>(labels.keySet());
        ArrayList<Integer> closest_indices = new ArrayList<>();

        int actual = values.size() + 1;

        for (int i = 0; i < indices.size(); i++) {
            closest_indices.add(Math.abs(indices.get(i) - zindex));
            actual = Math.min(actual, closest_indices.get(i));
        }

        for (int i = 0; i < indices.size(); i++) {
            if (Objects.equals(indices.get(i), indices.get(actual))
                    && i != 0
                    && i != indices.size() - 1) {
                prev = indices.get(i - 1);
                next = indices.get(i + 1);
                break;
            }
        }

        int delta = size / (int)divisions;

        piPlaceable &= pindex != -1 && Math.abs(values.get(pindex) - 3.14) < ROUGH_EPSILON;

        zeroPlaceable &= ((prev - zindex > delta / 2) || (zindex - next > delta / 2))
                && zindex != -1 && Math.abs(values.get(zindex)) < ROUGH_EPSILON;

        JLabel piLabel = new JLabel("\u03C0");
        piLabel.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        if (piPlaceable) {
            labels.put(pindex, piLabel);
        }

        JLabel zeroLabel = new JLabel("0.0");
        zeroLabel.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        if (zeroPlaceable) {
            labels.put(zindex, zeroLabel);
        }

        return labels;
    }

    private static SteppingSlider<Double> getDoubleCoefficientSlider(final double min,
                                                 final double max,
                                                 final double step,
                                                 final double divisions,
                                                 final boolean prevent) {
        ArrayList<Double> values = getDoubleCoefficientValues(min, max, step, divisions);

        return new SteppingSlider<>(values,
                getDoubleCoefficientSliderSimpleLabels(min, max, step, divisions),
                values.size() / 2,
                prevent);
    }

    private static SteppingSlider<Double> getDoubleCoefficientSlider(final double min,
                                                                     final double max,
                                                                     final double step) {
        final double strict = 0.1;
        final double rough = 0.2;

        int pindex = -1;
        int zindex = -1;

        int before = (int)((Math.abs(min - max)) / step);
        int dividable = before;

        if (dividable % 4 != 1)
            for (;; dividable++) {
                if (dividable % 4 == 1) {
                    break;
                }
            }

        double fmax = max + step * (dividable - before);

        boolean piFindable = fmax > 3.15;
        boolean zeroFindable = fmax > -0.01;

        ArrayList<Double> values = new ArrayList<>();

        int i = 0;

        for (double d = min; d < fmax; d = d + step) {
            values.add(d);

            if (piFindable)
                if (Math.abs(d - 3.14) < step) {
                    pindex = i;
                }

            if (zeroFindable)
                if (Math.abs(d) < step) {
                    zindex = i;
                }

            i++;
        }

        int sz = values.size();

        DecimalFormat format = new DecimalFormat("#.##");

        Hashtable<Integer, JLabel> table = new Hashtable<>();

        JLabel maxValue = new JLabel(
                String.format("%.2f", values.get(sz - 1))
        );
        maxValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        table.put(sz - 1, maxValue);

        JLabel upperQuarterValue = new JLabel(
                String.format("%.2f", values.get(sz / 2 + sz / 4))
        );
        upperQuarterValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        table.put(sz / 2 + sz / 4, upperQuarterValue);

        JLabel midValue = new JLabel(
                String.format("%.2f", values.get(sz / 2))
        );
        midValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        table.put(sz / 2, midValue);

        JLabel lowerQuarterValue = new JLabel(
                String.format("%.2f", values.get(sz / 4))
        );
        lowerQuarterValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        table.put(sz / 4, lowerQuarterValue);

        JLabel minValue = new JLabel(
                String.format("%.2f", values.get(0))
        );
        minValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        table.put(0, minValue);

        JLabel piValue = new JLabel("\u03C0");
        piValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));
        if (pindex != -1 && Math.abs(values.get(pindex) - 3.14) < rough)
            table.put(pindex, piValue);

        JLabel zeroValue = new JLabel("0");
        zeroValue.setFont(getDefaultFont(__SLIDER_FONT_SIZE));

        if (zindex != -1 && !midValue.getText().equals("0.0")
                && !upperQuarterValue.getText().equals("0.0")
                && !lowerQuarterValue.getText().equals("0.0")
                && !maxValue.getText().equals("0.0")
                && !minValue.getText().equals("0.0")
                && Math.abs(values.get(zindex)) < rough
        )

            table.put(zindex, zeroValue);

        return new SteppingSlider<>(values, table, sz / 2, true);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Range getComputationRange() {
        return COMPUTATION_RANGE;
    }

    public static int getAdditionButtonInvokeCount() {
        return additionButtonInvokeCount;
    }

    public static HashMap<String, Double> getCoefficientMap() {
        return coefficientMap;
    }

    public static double getDimensionMultiplier() {
        return __DIMENSION_MULTIPLIER;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Graph instance = new Graph();
            instance.setVisible(true);
        });
    }

    private static final String __NAME                  = "Grapher";
    private static final String __FONT                  = "Courier New";
    private static final int __FONT_BUTTON_SIZE         = 20;
    private static final int __BUTTON_HEIGHT            = 60;
    private static final int __MAGIC_WIDTH              = 66;
    private static final int __SLIDER_HEIGHT            = 366;
    private static final float __STROKE_WIDTH           = 2.0f;
    private static final String __IMAGE_NAME            = "logo.jpg";
    private static final ImageIcon __IMAGE_ICON         = new ImageIcon(__IMAGE_NAME);
    private static final Image __IMAGE                  = new ImageIcon(__IMAGE_NAME).getImage();
    private static final int __ZOOM_SLIDER_FONT_SIZE    = 13;
    private static final int __SLIDER_FONT_SIZE         = 13;
    private static final double __DIMENSION_MULTIPLIER  = 1.2;

    private static final Dimension DIMENSION = new Dimension(
            (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getWidth() / __DIMENSION_MULTIPLIER),

            (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getHeight() / __DIMENSION_MULTIPLIER));

    private static final ArrayList<Integer> ZOOM_VALUES = new ArrayList<>(Arrays.asList
            (
            1,
            3, 5, 7, 10, 12, 14, 16, 18, 20,
            25,
            30, 35, 40, 45, 50, 60, 70, 80, 90,
            100,
            125, 150, 175, 200, 225, 250, 275, 300, 350,
            400,
            450, 500, 550, 600, 650, 700, 750, 800,
            1000
            )
    );

    private static final double ASSUMABLE_INFINITY      = (double)ZOOM_VALUES.get(ZOOM_VALUES.size() - 1) * DEFAULT_X / 100.0;

    private static final Range COMPUTATION_RANGE        = new Range(-ASSUMABLE_INFINITY, ASSUMABLE_INFINITY);

    private static final int ZOOM_DEFAULT_INDEX         = ZOOM_VALUES.size() / 2;
    private static final int ZOOM_UPPER_QUARTER_INDEX   = ZOOM_DEFAULT_INDEX + ZOOM_VALUES.size() / 4;
    private static final int ZOOM_LOWER_QUARTER_INDEX   = ZOOM_DEFAULT_INDEX - ZOOM_VALUES.size() / 4;

    private static final ArrayList<Integer> COEFFICIENT_SLIDER_VALUES = new ArrayList<>(Arrays.asList
            (
            -20,
            -19, -18, -17, -16, -15, -14, -13, -12, -11,
            -10,
            -9, -8, -7, -6, -5, -4, -3, -2, -1,
            0,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            10,
            11, 12, 13, 14, 15, 16, 17, 18,
            20
            )
    );

    private static final double STRICT_EPSILON = 0.1;
    private static final double ROUGH_EPSILON = 0.2;

    private static final int COEFFICIENT_SLIDER_DEFAULT_INDEX       = COEFFICIENT_SLIDER_VALUES.size() / 2;
    private static final int COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX = COEFFICIENT_SLIDER_DEFAULT_INDEX + COEFFICIENT_SLIDER_VALUES.size() / 4;
    private static final int COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX = COEFFICIENT_SLIDER_DEFAULT_INDEX - COEFFICIENT_SLIDER_VALUES.size() / 4;

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