package ru.grapher;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.grapher.core.ChoiceBox;
import ru.grapher.core.ChoiceBoxUI;
import ru.grapher.core.DynamicButton;
import ru.grapher.menu.MainMenuBar;
import ru.grapher.slider.CoefficientSlider;
import ru.grapher.slider.ScopeSlider;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class StatelessGrapher extends JFrame {

    JFreeChart chart;
    ChartPanel chartPanel;
    JPanel     handlePanel;

    public static final class CoefficientBox extends ChoiceBox {
        CoefficientBox() {
            super(new ChoiceBoxUI());

            this.setFocusable(false);
            this.setFont(GUI.font(GUI.FONT_BUTTON_SIZE));
            this.setBackground(Color.WHITE);
            this.setBorder(GUI.__UNIVERSAL_BORDER);
        }
    }

    final JButton addButton        = new DynamicButton("Add", 20);
    final JButton clearButton      = new DynamicButton("Clear", 20);
    final JButton resetButton      = new DynamicButton("Reset", 20);
    final JButton calculatorButton = new DynamicButton("Calculator", 20);
    final JButton rangeButton      = new DynamicButton("Range", 20);

    final CoefficientBox coefficientBox = new CoefficientBox();

    final CoefficientSlider coefficientSlider = CoefficientSlider.createDefault();
    final ScopeSlider       scopeSlider       = ScopeSlider.createDefault();

    final Map<String, Double>        coefficientMap            = new HashMap<>();
    final XYSeriesCollection         currentXYSeriesCollection = new XYSeriesCollection();
    final Map<XYSeries, Set<String>> currentXYSeriesMap        = new ConcurrentHashMap<>();

    StatelessGrapher() {
        super();

        this.setPreferredSize(new Dimension(1250, 900));

        this.initChart();
        this.initHandlePanel();

        this.getContentPane().setLayout(new FlowLayout());

        this.getContentPane().add(chartPanel);
        this.getContentPane().add(handlePanel);

        this.initComponentActions();

        this.setupMenuBar();
        this.pack();

        this.initFrame();
    }

    private void initFrame() {
        this.setAlwaysOnTop(true);

        this.setTitle(GUI.NAME);
        this.setIconImage(GUI.LOGO);

        this.setLocationRelativeTo(null);

        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    abstract void initComponentActions();

    private void initChart() {
        chart = ChartBuild.createChart(currentXYSeriesCollection);

        chartPanel = new ChartPanel(chart) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
                super.paint(g2d);
            }
        };

        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setMouseZoomable(false);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.WHITE);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new MainMenuBar(this);
        this.setJMenuBar(menuBar);
    }

    private void initHandlePanel() {
        handlePanel = new JPanel();

        GroupLayout layout = new GroupLayout(handlePanel);

        handlePanel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(resetButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rangeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(calculatorButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(coefficientBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(coefficientSlider, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(scopeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(addButton, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rangeButton, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calculatorButton, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(coefficientBox, GroupLayout.PREFERRED_SIZE, GUI.BUTTON_HEIGHT, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(coefficientSlider, GroupLayout.DEFAULT_SIZE, GUI.SLIDER_HEIGHT, Short.MAX_VALUE)
                                        .addComponent(scopeSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
    }

    public void addCoefficients(Map<String, Double> coefficientMap) {
        this.coefficientBox.setItems(coefficientMap);
    }

    public CoefficientSlider getCoefficientSlider() {
        return coefficientSlider;
    }

    public Map<String, Double> getCoefficientMap() {
        return coefficientMap;
    }
}
