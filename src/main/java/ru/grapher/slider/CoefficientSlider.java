package ru.grapher.slider;

import ru.grapher.GUI;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class CoefficientSlider extends SteppingSlider<Double> {

    private static final double DEFAULT_MAXIMUM  = 10.0;
    private static final double DEFAULT_MINIMUM  = -13.0;
    private static final double DEFAULT_STEP     = 0.1;

    private static final boolean PAINT_TICKS     = false;

    public static final double VALUE_MULTIPLIER  = 1.0;
    public static final double DEFAULT_DIVISIONS = 8.0;

    public CoefficientSlider(final List<Double> domainValues,
                             final Hashtable<Integer, JLabel> labels,
                             final int defaultIndex) {
        super(domainValues, labels, defaultIndex, PAINT_TICKS);

        this.setOrientation(JSlider.VERTICAL);
        this.setEnabled(false);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(20, GUI.SLIDER_HEIGHT));
        this.setForeground(Color.BLACK);
        this.setBorder(GUI.__UNIVERSAL_BORDER);
        this.setFont(GUI.font(12));
    }

    private int getClosestDoubleIndex(final double domainValue) {
        List<Double> diffs = new ArrayList<>();

        for (double value : this.domainValues) {
            diffs.add(Math.abs(value - domainValue));
        }

        double[] min = {Math.abs(this.domainValues.getFirst() - domainValue), 0};

        for (int i = 0; i < diffs.size(); i++) {
            if (diffs.get(i) < min[0]) {
                min[0] = diffs.get(i);
                min[1] = i;
            }
        }

        return this.domainValues.contains(this.domainValues.get((int) min[1]))
                ? (int) min[1]
                : 0;
    }

    public void setClosestDomainValue(final double domainValue) {
        int closest = getClosestDoubleIndex(domainValue);

        if (closest != 0)
            this.setValue(closest);
        else
            this.setValue(this.size / 2);
    }

    public void setConfiguration(final double current,
                                 final double min,
                                 final double max,
                                 final double step,
                                 final double divisions) {
        super.setConfiguration(
                createValues(min, max, step, divisions),
                getClosestDoubleIndex(current),
                createSimpleLabels(min, max, step, divisions)
        );
    }

    public double[] getValues() {
        return new double[] {getMaximalValue(), getMinimalValue(), getStepValue()};
    }

    public double getMaximalValue() {
        return this.domainValues.getLast();
    }

    public double getMinimalValue() {
        return this.domainValues.getFirst();
    }

    public double getStepValue() {
        return Math.round(Math.abs(this.domainValues.get(0) - this.domainValues.get(1)) * 100.0) / 100.0;
    }

    public static List<Double> createValues(final double min,
                                            final double max,
                                            final double step,
                                            final double divisions) {
        List<Double> values = new ArrayList<>();

        int before = (int) ((Math.abs(min - max)) / step);
        int dividable = before;

        if (dividable % divisions != 0)
            for (; ; dividable++) {
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

    public static JLabel createLabel(final double value) {

        JLabel label = new JLabel(String.format("%.2f", value));
        label.setFont(GUI.sliderFont());
        return label;
    }

    public static Hashtable<Integer, JLabel> createSimpleLabels(final double min,
                                                                final double max,
                                                                final double step,
                                                                final double divisions) {

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        List<Double> values = createValues(min, max, step, divisions);

        int size = values.size();


        for (int index = size - 1; index >= 0; index -= size / (int) divisions) {
            JLabel label = createLabel(values.get(index));
            labels.put(index, label);
        }

        labels.put(0, createLabel(min));

        return labels;
    }

    public static CoefficientSlider createDefault() {
        List<Double> values = createValues(
                CoefficientSlider.DEFAULT_MINIMUM,
                CoefficientSlider.DEFAULT_MAXIMUM,
                CoefficientSlider.DEFAULT_STEP,
                CoefficientSlider.DEFAULT_DIVISIONS);

        return new CoefficientSlider(
                values,
                createSimpleLabels(
                        CoefficientSlider.DEFAULT_MINIMUM,
                        CoefficientSlider.DEFAULT_MAXIMUM,
                        CoefficientSlider.DEFAULT_STEP,
                        CoefficientSlider.DEFAULT_DIVISIONS),
                values.size() / 2
        );
    }
}
