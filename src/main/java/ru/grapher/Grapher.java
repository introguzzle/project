package ru.grapher;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.calculator.Calculator;
import ru.mathparser.MathFunctionParser;
import ru.grapher.menu.AboutFrame;
import ru.mathparser.MathParser;
import ru.mathparser.MathParserException;
import ru.grapher.menu.HelpFrame;
import ru.utils.ColorUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;

public class Grapher extends JFrame {

    public static final class RunConfiguration {

        public static final int     PRECISION_DIGITS        = 2;
        public static final Mode    MODE                    = Mode.HALF;

        public static final boolean TRY_OPTIMIZATION        = false;

        public static final boolean ASK_CONFIRMATION        = true;
        public static final boolean EVEN_ADJUST             = false;


        public static final double  NUMBER_OF_TICKS         = 10.0;
        public static final double  SCALE_TO_SQUARE_FACTOR  = 1.25; // 1024 / 768 is 1.33, but 1.33 is odd value, so using 5 / 4 resolution instead of 4 / 3

        public static final double  DEFAULT_X               = 10.0;
        public static final Range   DEFAULT_RANGE_OF_X      = new Range(-DEFAULT_X, DEFAULT_X);
        public static final double  DEFAULT_Y               = 8.0;
        public static final Range   DEFAULT_RANGE_OF_Y      = new Range(-DEFAULT_Y, DEFAULT_Y);

        // we define this like this cuz we don't need to compute X values
        // outside visible plot
        // for example: default X is 8 and maximum scope is 1000%
        // so bound of computation is [-80, 80]

        public static final double  COMPUTATION_BOUND       = (double)ScopeSlider.DefaultConfiguration.
                SCOPE_DOMAIN_VALUES.getLast() * DEFAULT_X / 100.0;
        public static final Range   COMPUTATION_RANGE       = new Range(-COMPUTATION_BOUND, COMPUTATION_BOUND);

        public static final List<Double> X_VALUES = new ArrayList<>();

        static {
            final double step = 1 / Math.pow(10, PRECISION_DIGITS);

            for (double d = -COMPUTATION_BOUND;
                 d <= COMPUTATION_BOUND;
                 d += step) {
                X_VALUES.add(d);
            }
        }
    }

    private static final Logger logger              = Logger.getLogger("ru.grapher");

    private static final JButton additionButton     = new DynamicButton("Add", 20);
    private static final JButton clearingButton     = new DynamicButton("Clear", 20);
    private static final JButton resettingButton    = new DynamicButton("Reset", 20);
    private static final JButton calculatorButton   = new DynamicButton("Calculator", 20);
    private static final JButton rangeButton        = new DynamicButton("Range", 20);
    private static final JButton adjustButton       = new DynamicButton("Adjust", 20);


    private static final    JComboBox<String>    coefficientBox      = new JComboBox<>();

    private static          CoefficientSlider    coefficientSlider   = new CoefficientSlider();
    private static          ScopeSlider          scopeSlider         = new ScopeSlider();


    private static boolean  sliderSwitcher          = true;
    private static boolean  handlePanelSwitcher     = true;

    private static double   currentScope            = 1.0;
    private static String   currentCoefficient      = null;

    private static double   xShift                  = 1.0;
    private static double   yShift                  = 1.0;

    private static          String[]                    coefficientArray    = new String[]{};
    private static final    Map<String, Double>         coefficientMap      = new HashMap<>();

    private static int      additionButtonInvokeCount = 0;

    private static String   previousInput;

    private static JDialog  rangeDialog;

    private static final RangePanel rangePanel = new RangePanel(
            CoefficientSlider.DefaultConfiguration.DEFAULT_MAXIMUM,
            CoefficientSlider.DefaultConfiguration.DEFAULT_MINIMUM,
            CoefficientSlider.DefaultConfiguration.DEFAULT_STEP
    );

    private static final    FunctionPanel       functionPanel = new FunctionPanel();

    private static          String firstResponse;
    private static          String secondResponse;

    private static final XYSeriesCollection
            currentXYSeriesCollection   = new XYSeriesCollection();

    private static final ConcurrentHashMap<XYSeries, List<String>>
            currentXYSeriesMap          = new ConcurrentHashMap<>();

    private static final  List<Double>  rangeMaxValues         = new ArrayList<>();
    private static double rangeMaxValuesMinimum;

    private static final  List<Double>  rangeMinValues         = new ArrayList<>();
    private static double rangeMinValuesMinimum;


    private static double modifiedLowerY    = -RunConfiguration.DEFAULT_Y;
    private static double modifiedUpperY    = RunConfiguration.DEFAULT_Y;

    private static double modifiedLowerX    = -RunConfiguration.DEFAULT_X;
    private static double modifiedUpperX    = RunConfiguration.DEFAULT_X;

    private static void reinitXYSeriesCollection() {

        currentXYSeriesCollection.removeAllSeries();

        for (var entry: currentXYSeriesMap.entrySet()) {
            if (entry.getValue().contains(currentCoefficient)) {
                currentXYSeriesCollection.addSeries(createXYSeriesRealTime(entry.getKey().getDescription()));
            } else {
                currentXYSeriesCollection.addSeries(entry.getKey());
            }
        }
    }

    private static void addToXYSeriesCollection(final XYSeries series) {
        List<String> coefficients = MathFunctionParser.Explicit.getCoefficients(series.getDescription());

        if (Collections.indexOfSubList(new ArrayList<>(Arrays.asList(coefficientArray)), coefficients) != -1) {
            try {
                currentXYSeriesCollection.addSeries(series);
            } catch (IllegalArgumentException ignored) {

            }
            currentXYSeriesMap.put(series, coefficients);

            if (RunConfiguration.EVEN_ADJUST)
                updateMinMax(series);
        }
    }

    private static void addParametricToXYSeriesCollection(final XYSeries series) {
        List<String> coefficients = MathFunctionParser.Explicit.getCoefficients(series.getDescription());

        currentXYSeriesCollection.addSeries(series);
        currentXYSeriesMap.put(series, coefficients);
    }

    private static void updateXYSeriesCollection() {

        currentXYSeriesCollection.removeAllSeries();

        for (var entry: currentXYSeriesMap.entrySet()) {
            try {
                currentXYSeriesCollection.addSeries(createXYSeriesRealTimeStream(entry.getKey().getDescription()));
            } catch (IllegalArgumentException ignored) {

            }
        }

    }

    public Grapher() {
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

        coefficientSlider = CoefficientSlider.Utility.create(
                CoefficientSlider.DefaultConfiguration.DEFAULT_MINIMUM,
                CoefficientSlider.DefaultConfiguration.DEFAULT_MAXIMUM,
                CoefficientSlider.DefaultConfiguration.DEFAULT_STEP,
                CoefficientSlider.DefaultConfiguration.DIVISIONS
        );

        coefficientSlider.setEnabled(false);
        coefficientSlider.setPaintLabels(true);
        coefficientSlider.setOrientation(JSlider.VERTICAL);
        coefficientSlider.setFocusable(false);
        coefficientSlider.setBackground(Color.WHITE);
        coefficientSlider.setPreferredSize(new Dimension(20, GrapherGUI.__SLIDER_HEIGHT));
        coefficientSlider.setForeground(Color.BLACK);
        coefficientSlider.setBorder(GrapherGUI.__UNIVERSAL_BORDER);
        coefficientSlider.setFont(GrapherGUI.getDefaultFont(12));

        coefficientSlider.addMouseListener(createCoefficientSliderMouseAdapter());

        scopeSlider = new ScopeSlider(
                ScopeSlider.DefaultConfiguration.SCOPE_DOMAIN_VALUES,
                ScopeSlider.Utilities.createScopeTable(),
                ScopeSlider.DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 2
        );

        scopeSlider.setPaintLabels(true);
        scopeSlider.setOrientation(JSlider.VERTICAL);
        scopeSlider.setFocusable(false);
        scopeSlider.setBackground(Color.WHITE);
        scopeSlider.setThumbColor(Color.RED);
        scopeSlider.setPreferredSize(new Dimension(20, GrapherGUI.__SLIDER_HEIGHT));
        scopeSlider.setForeground(Color.BLACK);
        scopeSlider.setBorder(GrapherGUI.__UNIVERSAL_BORDER);
        scopeSlider.setFont(GrapherGUI.getDefaultFont(12));

        scopeSlider.addChangeListener(e -> {

            currentScope = (double) scopeSlider.getDomainValue() / 100;

            NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
            NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

            if (GrapherUtilities.isCorrectValue(modifiedLowerX * currentScope)
                    && GrapherUtilities.isCorrectValue(modifiedUpperX * currentScope)) {

                xAxis.setRange(new Range(modifiedLowerX * currentScope, modifiedUpperX * currentScope));

                yAxis.setRange(new Range(modifiedLowerY * currentScope, modifiedUpperY * currentScope));
            }

            GrapherUtilities.normalizeTick(xAxis, "x");
            GrapherUtilities.normalizeTick(yAxis, "y");

            xShift = Objects.requireNonNull(GrapherUtilities.getNormalNumberTickUnit(
                    (NumberAxis) chart.getXYPlot().
                            getDomainAxis(), "x")).getSize();

            yShift = Objects.requireNonNull(GrapherUtilities.getNormalNumberTickUnit(
                    (NumberAxis) chart.getXYPlot().
                            getRangeAxis(), "y")).getSize();

        });

        additionButton.addActionListener((new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JFrame instance = (Grapher) SwingUtilities.getWindowAncestor((JButton) evt.getSource());

                JDialog inputDialog = new JDialog(
                        instance,
                        "Function Input",
                        true);

                inputDialog.setResizable(false);
                inputDialog.setIconImage(GrapherGUI.__IMAGE);


                additionButtonInvokeCount++;

                inputDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent evt) {

                        if (secondResponse == null)
                            setPreviousInput(functionPanel.getFirstResponse());

                        functionPanel.setInterrupted(true);
                        functionPanel.resetPanel();
                        inputDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    }
                });

                inputDialog.setFocusTraversalKeysEnabled(false);
                inputDialog.getContentPane().add(functionPanel);
                inputDialog.pack();

                inputDialog.setIconImage(GrapherGUI.__IMAGE);
                inputDialog.setLocationRelativeTo(instance);
                inputDialog.setVisible(true);
                inputDialog.setResizable(false);

                firstResponse  = functionPanel.getFirstResponse();
                secondResponse = functionPanel.getSecondResponse();

                if (functionPanel.getSecondResponse().isEmpty()) {
                    if (functionPanel.isDone() && !functionPanel.isInterrupted()) {

                        functionPanel.setDone(true);
                        functionPanel.resetPanel();

                        inputDialog.dispose();
                        inputDialog.dispatchEvent(new WindowEvent(inputDialog, WindowEvent.WINDOW_CLOSING));


                        coefficientMap.putAll(functionPanel.getCoefficientDoubleMap());

                        coefficientArray = coefficientMap.keySet().toArray(new String[0]);

                        if (additionButtonInvokeCount > 0 && !coefficientMap.isEmpty()) {
                            coefficientBox.setEnabled(true);
                            coefficientBox.setModel(new DefaultComboBoxModel<>(coefficientArray));

                            coefficientSlider.setEnabled(true);
                            double value = coefficientMap.get((String) coefficientBox.getSelectedItem());

                            coefficientSlider.setClosestDomainValue(value);
                        }

                        if (MathFunctionParser.Explicit.getCoefficients(firstResponse).isEmpty()) {
                            if (RunConfiguration.TRY_OPTIMIZATION)
                                addToXYSeriesCollection(createXYSeriesRealTimeStream(firstResponse));
                            else
                                addParametricToXYSeriesCollection(createXYSeriesRealTimeStream(firstResponse));

                        } else {
                            if (RunConfiguration.TRY_OPTIMIZATION) {
                                addToXYSeriesCollection(createXYSeriesRealTimeStream(firstResponse));
                                updateXYSeriesCollection();
                            } else {
                                System.out.println("ffff");
                                addParametricToXYSeriesCollection(createXYSeriesRealTimeStream(firstResponse));
                                updateXYSeriesCollection();
                            }
                        }
                    }
                } else {
                    if (functionPanel.isDone() && !functionPanel.isInterrupted()) {

                        functionPanel.setDone(true);
                        functionPanel.resetPanel();

                        inputDialog.dispose();
                        inputDialog.dispatchEvent(new WindowEvent(inputDialog, WindowEvent.WINDOW_CLOSING));

                        coefficientMap.putAll(functionPanel.getCoefficientDoubleMap());

                        coefficientArray = coefficientMap.keySet().toArray(new String[0]);

                        if (additionButtonInvokeCount > 0 && !coefficientMap.isEmpty()) {
                            coefficientBox.setEnabled(true);
                            coefficientBox.setModel(new DefaultComboBoxModel<>(coefficientArray));

                            coefficientSlider.setEnabled(true);
                            double value = coefficientMap.get((String) coefficientBox.getSelectedItem());

                            coefficientSlider.setClosestDomainValue(value);
                        }

                        if (functionPanel.getCoefficientDoubleMap().isEmpty()) {
                            addParametricToXYSeriesCollection(createParametricXYSeriesRealTimeStream(firstResponse, secondResponse));

                        } else {
                            addParametricToXYSeriesCollection(createParametricXYSeriesRealTimeStream(firstResponse, secondResponse));
                            updateXYSeriesCollection();
                        }
                    }
                }
            }
        }));

        clearingButton.addActionListener(evt -> {

            currentXYSeriesCollection.removeAllSeries();
            currentXYSeriesMap.clear();

            GrapherUtilities.resetAxes(chart);
            GrapherUtilities.resetSlider(scopeSlider);

            coefficientBox.setEnabled(false);
            coefficientBox.setModel(InputFunctionPanel.getDefaultComboBoxModel());

            coefficientSlider.setEnabled(false);
        });

        resettingButton.addActionListener(evt -> {

            GrapherUtilities.resetAxes(chart);
            GrapherUtilities.resetSlider(scopeSlider);
        });

        rangeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JFrame instance = (Grapher) SwingUtilities.getWindowAncestor((JButton) evt.getSource());

                if (rangeDialog == null) {
                    rangeDialog = new JDialog(
                            instance,
                            "Set Range",
                            true
                    );

                    rangeDialog.setIconImage(GrapherGUI.__IMAGE);
                    rangeDialog.setResizable(false);
                }

                rangeDialog.getContentPane().add(rangePanel);
                rangeDialog.pack();

                rangeDialog.setLocationRelativeTo(instance);
                rangeDialog.setVisible(true);
                rangeDialog.setIconImage(GrapherGUI.__IMAGE);
                rangeDialog.setResizable(false);

                if (rangePanel.isDone()) {

                    double min = Double.parseDouble(rangePanel.getMinimalValueField().getText());
                    double max = Double.parseDouble(rangePanel.getMaximalValueField().getText());
                    double step = Double.parseDouble(rangePanel.getStepField().getText());


                    List<Double> values = CoefficientSlider.Utility.
                            createValues(min, max, step, CoefficientSlider.DefaultConfiguration.DIVISIONS);

                    Hashtable<Integer, JLabel> labels = CoefficientSlider.Utility.
                            createSimpleLabels(min, max, step, CoefficientSlider.DefaultConfiguration.DIVISIONS);

                    coefficientSlider.setConfiguration(
                            values,
                            coefficientSlider.getValue(),
                            labels
                    );

                    rangePanel.setDone(true);
                }
            }
        });

        rangeButton.setText("Range");

        adjustButton.setText("Adjust");

        adjustButton.addActionListener(evt -> {

            if (!rangeMaxValues.isEmpty()) {
                NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();
                NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                yAxis.setRange(new Range(rangeMinValuesMinimum, rangeMaxValuesMinimum));

                modifiedLowerY = rangeMinValuesMinimum;
                modifiedUpperY = rangeMaxValuesMinimum;

                modifiedLowerX = xAxis.getLowerBound();
                modifiedUpperX = xAxis.getUpperBound();

                GrapherUtilities.normalizeTick(xAxis, "x");
                GrapherUtilities.normalizeTick(yAxis, "y");
            }
        });

        calculatorButton.setText("Calculator");

        calculatorButton.addActionListener(evt -> {

            JFrame frame = new Calculator();

            frame.setLocationRelativeTo(null);
            frame.setAlwaysOnTop(true);
            frame.setTitle("Calculator");
            frame.setIconImage(GrapherGUI.__IMAGE);

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            frame.setVisible(true);
        });

        coefficientBox.setUI(new ChoiceBoxUI());
        coefficientBox.setFocusable(false);
        coefficientBox.setEnabled(false);
        coefficientBox.setFont(GrapherGUI.getDefaultFont(GrapherGUI.__FONT_BUTTON_SIZE));
        coefficientBox.setBackground(Color.WHITE);
        coefficientBox.setBorder(GrapherGUI.__UNIVERSAL_BORDER);

        coefficientBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                double value = coefficientMap.get((String) coefficientBox.getSelectedItem());
                coefficientSlider.setClosestDomainValue(value);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(GrapherGUI.getDefaultFont(22));

        JMenu menu = new JMenu("Menu  ");

        menu.setFont(GrapherGUI.getDefaultFont(22));
        menu.getPopupMenu().setBorder(GrapherGUI.__UNIVERSAL_BORDER);

        JMenuItem helpMenu  = createHelpMenuItem();
        JMenuItem aboutMenu = createAboutMenuItem();
        JMenuItem exitMenu  = createExitMenuItem();

        menu.add(helpMenu);
        menu.add(aboutMenu);
        menu.addSeparator();
        menu.add(exitMenu);

        menuBar.add(menu);
        menuBar.setPreferredSize(aboutMenu.getPreferredSize());
        menuBar.setMinimumSize(aboutMenu.getMinimumSize());

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
                                        .addComponent(calculatorButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(coefficientBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(coefficientSlider, GroupLayout.PREFERRED_SIZE, GrapherGUI.__MAGIC_WIDTH, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(scopeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(additionButton, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearingButton, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resettingButton, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rangeButton, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calculatorButton, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(coefficientBox, GroupLayout.PREFERRED_SIZE, GrapherGUI.__BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(coefficientSlider, GroupLayout.DEFAULT_SIZE, GrapherGUI.__SLIDER_HEIGHT, Short.MAX_VALUE)
                                        .addComponent(scopeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        if (RunConfiguration.ASK_CONFIRMATION) {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent evt) {
                    JDialog exitDialog = new JDialog((Grapher) evt.getSource(),
                            GrapherGUI.__NAME, true);
                    exitDialog.add(new ExitPanel(true));

                    exitDialog.pack();

                    JFrame instance = (Grapher) evt.getSource();
                    instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                    exitDialog.setIconImage(GrapherGUI.__IMAGE);
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

                if (KEY == KeyEvent.VK_RIGHT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (GrapherUtilities.isCorrectValue(xAxis.getUpperBound() + xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() + xShift, xAxis.getUpperBound() + xShift));
                        GrapherUtilities.normalizeTick(xAxis, "x");
                    }

                    modifiedLowerX = xAxis.getLowerBound();
                    modifiedUpperX = xAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_LEFT) {
                    NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

                    if (GrapherUtilities.isCorrectValue(xAxis.getLowerBound() - xShift)) {
                        xAxis.setRange(new Range(xAxis.getLowerBound() - xShift, xAxis.getUpperBound() - xShift));
                        GrapherUtilities.normalizeTick(xAxis, "x");
                    }

                    modifiedLowerX = xAxis.getLowerBound();
                    modifiedUpperX = xAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_UP) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() + yShift, yAxis.getUpperBound() + yShift));
                    GrapherUtilities.normalizeTick(yAxis, "y");

                    modifiedLowerY = yAxis.getLowerBound();
                    modifiedUpperY = yAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_DOWN) {
                    NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();

                    yAxis.setRange(new Range(yAxis.getLowerBound() - yShift, yAxis.getUpperBound() - yShift));
                    GrapherUtilities.normalizeTick(yAxis, "y");

                    modifiedLowerY = yAxis.getLowerBound();
                    modifiedUpperY = yAxis.getUpperBound();
                }

                if (KEY == KeyEvent.VK_0) {
                    if (sliderSwitcher) {
                        sliderSwitcher = false;
                        coefficientSlider.setThumbColor(Color.RED);
                        scopeSlider.setThumbColor(Color.BLACK);

                    } else {
                        sliderSwitcher = true;
                        coefficientSlider.setThumbColor(Color.BLACK);
                        scopeSlider.setThumbColor(Color.RED);
                    }
                }

                if (KEY == KeyEvent.VK_MINUS) {
                    if (sliderSwitcher)
                        scopeSlider.setValue(scopeSlider.getValue() - 1);
                }

                if (KEY == KeyEvent.VK_EQUALS) {
                    if (sliderSwitcher)
                        scopeSlider.setValue(scopeSlider.getValue() + 1);
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
                    createExitMenuItem().doClick();
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
                    calculatorButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    adjustButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    resettingButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    clearingButton.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    scopeSlider.setBackground(new Color((int)(Math.random() * 0x1000000)));
                    coefficientSlider.setBackground(new Color((int)(Math.random() * 0x1000000)));
                }

                if (KEY == KeyEvent.VK_3) {
                    int r = (int)(Math.random() * 255);
                    int g = (int)(Math.random() * 255);
                    int b = (int)(Math.random() * 255);

                    Color style1 = new Color(r, g, b);

                    Color style2 = ColorUtilities.constrastingOf(style1);

                    chartPanel.setBackground(style1);
                    chartPanel.setForeground(style2);

                    additionButton.setBackground(style1);
                    additionButton.setForeground(style2);

                    calculatorButton.setBackground(style1);
                    calculatorButton.setForeground(style2);

                    adjustButton.setBackground(style1);
                    adjustButton.setForeground(style2);

                    resettingButton.setBackground(style1);
                    resettingButton.setForeground(style2);

                    clearingButton.setBackground(style1);
                    clearingButton.setForeground(style2);

                    coefficientBox.setBackground(style1);
                    coefficientBox.setForeground(style2);

                    scopeSlider.setBackground(style1);
                    scopeSlider.setForeground(style2);


                    coefficientSlider.setBackground(style1);
                    coefficientSlider.setForeground(style2);
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                final int KEY = evt.getKeyCode();

                if (KEY == KeyEvent.VK_MINUS && !sliderSwitcher && coefficientSlider.isEnabled()) {
                    coefficientSlider.setValue(coefficientSlider.getValue() - 1);
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(
                            currentCoefficient,
                            coefficientSlider.getDomainValue() * CoefficientSlider.DefaultConfiguration.VALUE_MULTIPLIER
                    );
                    updateXYSeriesCollection();
                }

                if (KEY == KeyEvent.VK_EQUALS && !sliderSwitcher && coefficientSlider.isEnabled()) {
                    coefficientSlider.setValue(coefficientSlider.getValue() + 1);
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(
                            currentCoefficient,
                            coefficientSlider.getDomainValue() * CoefficientSlider.DefaultConfiguration.VALUE_MULTIPLIER
                    );
                    updateXYSeriesCollection();
                }
            }
        });

        this.add(handlePanel);
        this.setAlwaysOnTop(true);

        this.pack();

        this.setTitle(GrapherGUI.__NAME);
        this.setIconImage(GrapherGUI.__IMAGE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setPreferredSize(GrapherGUI.__RESOLUTION);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static JMenuItem createHelpMenuItem() {
        JMenuItem helpMenu = new JMenuItem("Help");

        helpMenu.setFont(GrapherGUI.getDefaultFont(22));
        helpMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JFrame parent = (JFrame)
                        ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).
                                getInvoker().
                                getParent().
                                getParent().
                                getParent().
                                getParent();

                HelpFrame helpFrame = new HelpFrame(parent);

                parent.setEnabled(false);

                helpFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowIconified(WindowEvent e) {
                        JFrame instance = (JFrame) e.getSource();
                        // instance.dispatchEvent(new WindowEvent(instance, WindowEvent.WINDOW_CLOSING));
                        parent.setEnabled(true);
                        instance.dispose();
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        parent.setEnabled(true);
                    }
                });

                helpFrame.setLocationRelativeTo(null);
                helpFrame.setAlwaysOnTop(true);
                helpFrame.setTitle("Help");
                helpFrame.setIconImage(GrapherGUI.__IMAGE);

                helpFrame.setVisible(true);
            }
        });

        return helpMenu;
    }

    private static JMenuItem createAboutMenuItem() {

        JMenuItem aboutMenu = new JMenuItem("About");

        aboutMenu.setFont(GrapherGUI.getDefaultFont(22));
        aboutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AboutFrame instance = new AboutFrame();

                JFrame parent = (JFrame)
                        ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).
                                getInvoker().
                                getParent().
                                getParent().
                                getParent().
                                getParent();

                parent.setEnabled(false);

                instance.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowIconified(WindowEvent e) {
                        JFrame instance = (JFrame) e.getSource();
                        parent.setEnabled(true);
                        instance.dispose();
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        parent.setEnabled(true);
                    }
                });

                instance.setAlwaysOnTop(true);
                instance.setVisible(true);
            }
        });

        return aboutMenu;
    }

    private static JMenuItem createExitMenuItem() {

        JMenuItem exitMenu = new JMenuItem("Exit");

        exitMenu.setFont(GrapherGUI.getDefaultFont(22));
        if (RunConfiguration.ASK_CONFIRMATION) {
            exitMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JDialog exitDialog = new JDialog(
                            (JFrame) ((JMenuItem) evt.getSource()).getParent().getParent(),
                            GrapherGUI.__NAME,
                            true
                    );

                    exitDialog.add(new ExitPanel(false));

                    exitDialog.pack();

                    exitDialog.setIconImage(GrapherGUI.__IMAGE);
                    exitDialog.setResizable(false);
                    exitDialog.setAlwaysOnTop(true);
                    exitDialog.setLocationRelativeTo(null);

                    exitDialog.setVisible(true);
                }
            });
        }

        return exitMenu;
    }

    private static boolean containsAnyIn(final List<String> list) {
        for (var s: list) {
            if (Grapher.coefficientMap.containsKey(s))
                return true;
        }

        return false;
    }

    public static double parametricCompute(final String expression,
                                           final double value) {

        String changed = MathFunctionParser.Parametric.replaceVariable(expression, value);

        changed = MathParser.replaceConstants(changed);

        HashSet<String> coefficientsOfThis = MathFunctionParser.Parametric.getCoefficients(changed);

        if (!Grapher.coefficientMap.isEmpty()) {
            for (Map.Entry<String, Double> entry : Grapher.coefficientMap.entrySet()) {

                String coefficient  = entry.getKey();
                double v            = entry.getValue();

                if (containsAnyIn(new ArrayList<>(coefficientsOfThis))) {
                    changed = MathFunctionParser.Parametric.replaceCoefficient(changed, coefficient, v);
                }
            }
        }

        return MathParser.parse(changed);
    }

    public static double compute(final String expression,
                                 final double value) {

        String changed = expression;

        changed = MathFunctionParser.Explicit.replaceVariable(changed, value);
        changed = MathParser.replaceConstants(changed);

        List<String> coefficientsOfThis = MathFunctionParser.Explicit.getCoefficients(changed);

        if (!Grapher.coefficientMap.isEmpty()) {
            for (Map.Entry<String, Double> entry : Grapher.coefficientMap.entrySet()) {

                String coefficient  = entry.getKey();
                double v            = entry.getValue();

                if (containsAnyIn(coefficientsOfThis)) {
                    changed = MathFunctionParser.Explicit.replaceCoefficient(changed, coefficient, v);
                }
            }
        }

        return MathParser.parse(changed);
    }

    private static XYSeries createParametricXYSeriesRealTimeStream(final String xt,
                                                                   final String yt) {

        String key = "x(t) = " + xt + ", y(t) = " + yt;

        XYSeries series = new XYSeries(key, false, true);
        series.setDescription(key);

        ArrayList<XYDataItem> values = new ArrayList<>();

        RunConfiguration.X_VALUES.stream().parallel().forEachOrdered(t -> {
            try {
                values.add(new XYDataItem((Double)parametricCompute(xt, t), (Double) parametricCompute(yt, t)));

            } catch (IndexOutOfBoundsException e) {
                logger.log(Level.SEVERE, "index out of bounds", e);
            } catch (MathParserException e) {
                logger.log(Level.SEVERE, "parsing error", e);
            }
        });

        values.forEach(series::add);

        return series;
    }

    private static XYSeries createXYSeriesRealTimeStream(final String function) {

        XYSeries series = new XYSeries(function, true, true);
        series.setDescription(function);

        RunConfiguration.X_VALUES.stream().parallel().forEachOrdered(x -> {
            try {
                series.add(x, (Double) compute(function, x));
            } catch (IndexOutOfBoundsException e) {
                logger.log(Level.SEVERE, "index out of bounds", e);
            } catch (MathParserException e) {
                logger.log(Level.SEVERE, "parsing error", e);
            }
        });

        return series;
    }

    private static XYSeries createXYSeriesRealTime(final String function) {
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

        final double delta = RunConfiguration.COMPUTATION_RANGE.getLength() / THREADS_COUNT;

        final double step = 1 / Math.pow(10, RunConfiguration.PRECISION_DIGITS);

        for (double p = -RunConfiguration.COMPUTATION_BOUND; p <= RunConfiguration.COMPUTATION_BOUND; p = p + delta) {
            final double fp = p;
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    for (double x = fp; x < fp + delta; x = x + step) {
                        try {
                            series.add(x, compute(function, x));
                        } catch (MathParserException e) {
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

    public static int getThreads() {
        if (RunConfiguration.MODE == Mode.ALL)
            return Runtime.getRuntime().availableProcessors();
        else if (RunConfiguration.MODE == Mode.HALF)
            return Runtime.getRuntime().availableProcessors() / 2;
        else if (RunConfiguration.MODE == Mode.SINGLE)
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
        xAxis.setRange(-RunConfiguration.DEFAULT_X, RunConfiguration.DEFAULT_X);
        xAxis.setAutoTickUnitSelection(true);
        xAxis.setTickUnit(new NumberTickUnit(RunConfiguration.DEFAULT_X / RunConfiguration.NUMBER_OF_TICKS));

        NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
        yAxis.setRange(-RunConfiguration.DEFAULT_Y, RunConfiguration.DEFAULT_Y);
        yAxis.setAutoTickUnitSelection(true);
        yAxis.setTickUnit(new NumberTickUnit(RunConfiguration.DEFAULT_Y / (RunConfiguration.NUMBER_OF_TICKS / RunConfiguration.SCALE_TO_SQUARE_FACTOR)));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, new Color(
                (int)(Math.random() * 100),
                (int)(Math.random() * 100),
                (int)(Math.random() * 100))
        );

        renderer.setSeriesStroke(0, new BasicStroke(GrapherGUI.__STROKE_WIDTH));
        renderer.setSeriesShapesVisible(0, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        return chart;
    }

    private static MouseAdapter createCoefficientSliderMouseAdapter() {

        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (coefficientSlider.isEnabled()) {
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(
                            currentCoefficient,
                            coefficientSlider.getDomainValue() * CoefficientSlider.DefaultConfiguration.VALUE_MULTIPLIER
                    );
                    updateXYSeriesCollection();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (coefficientSlider.isEnabled()) {
                    currentCoefficient = (String) coefficientBox.getSelectedItem();
                    coefficientMap.put(
                            currentCoefficient,
                            coefficientSlider.getDomainValue() * CoefficientSlider.DefaultConfiguration.VALUE_MULTIPLIER
                    );
                    updateXYSeriesCollection();
                }
            }
        };
    }

    private static void updateMinMax(final XYSeries series) {
        rangeMaxValues.add(series.getMaxY());
        rangeMinValues.add(series.getMinY());
        rangeMinValuesMinimum = series.getMinY();
        rangeMaxValuesMinimum = series.getMaxY();

        if (GrapherUtilities.isCorrectValue(series.getMaxY()))
            rangeMaxValuesMinimum = rangeMaxValues.stream().min(Double::compare).orElse(1.0);
        else
            rangeMaxValuesMinimum = RunConfiguration.COMPUTATION_BOUND;

        if (GrapherUtilities.isCorrectValue(series.getMinY()))
            rangeMinValuesMinimum = rangeMinValues.stream().min(Double::compare).orElse(-1.0);
        else
            rangeMinValuesMinimum = -RunConfiguration.COMPUTATION_BOUND;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getPreviousInput() {
        return previousInput;
    }

    public static void setPreviousInput(final String newInput) {
        previousInput = newInput;
    }

    public static void setModifiedLowerY(double value) {
        modifiedLowerY = value;
    }

    public static void setModifiedUpperY(double value) {
        modifiedUpperY = value;
    }

    public static void setModifiedLowerX(double value) {
        modifiedLowerX = value;
    }

    public static void setModifiedUpperX(double value) {
        modifiedUpperX = value;
    }

    public static int getAdditionButtonInvokeCount() {
        return additionButtonInvokeCount;
    }

    public static Map<String, Double> getCoefficientMap() {
        return coefficientMap;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Grapher instance = new Grapher();
            instance.setVisible(true);
        });
    }
}