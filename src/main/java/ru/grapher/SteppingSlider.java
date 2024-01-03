package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public final class SteppingSlider<T> extends JSlider {

    private ArrayList<T> values = new ArrayList<>();
    private int size;

    public SteppingSlider() {
    }

    public SteppingSlider(final ArrayList<T> values,
                          final Hashtable<Integer, JLabel> labels,
                          final int defaultIndex) {
        super(0, values.size() - 1, defaultIndex);
        this.setUI(new SteppingSliderUI(this, Color.BLACK));
        this.setLabelTable(labels);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setSnapToTicks(true);
        this.setMajorTickSpacing(1);
        this.setFocusable(false);

        this.values = values;
        this.size = values.size();
    }

    public SteppingSlider(final ArrayList<T> values,
                          final Hashtable<Integer, JLabel> labels,
                          final int defaultIndex,
                          final boolean prevent) {
        this(values, labels, defaultIndex);
        this.setPaintTicks(!prevent);
    }

    public void setConfiguration(final ArrayList<T> values,
                                 final int current,
                                 final Hashtable<Integer, JLabel> labels) {
        this.setMinimum(0);
        this.setMaximum(values.size() - 1);
        this.setValue(current);

        this.setLabelTable(labels);

        this.values = values;
        this.size = values.size();
    }

    // must be same type,
    // but maybe I could write this using wildcard as
    // public void setConfiguration(SteppingSlider<?> other) {
    //
    //

    public void setConfiguration(final SteppingSlider<T> other,
                                 final int defaultIndex,
                                 final Hashtable<Integer, JLabel> newLabels) {
        this.setMinimum(0);
        this.setMaximum(other.values.size() - 1);
        this.setValue(defaultIndex);

        this.setLabelTable(newLabels);

        this.values = other.values;
        this.size = other.values.size();
    }

    public void setThumbColor(Color thumbColor) {
        this.setUI(new SteppingSliderUI(this, thumbColor));
    }

    public ArrayList<T> getValues() {
        return this.values;
    }

    public T getDomainValue() {
        return this.values.get(this.getValue());
    }

    public void setDomainValue(final T domainValue) {
        for (int i = 0; i < size; i++) {
            if (domainValue == this.values.get(i))
                this.setValue(i);
        }
    }

    private int getClosestDoubleIn(final double domainValue) {
        final double epsilon = 1.0 / Math.pow(10.0, 2.0);
        final double extreme_epsilon = 1.0;

        Map<Double, Integer> differences = new HashMap<>();
        Map<Double, Integer> rough_differences = new HashMap<>();

        for (int i = 0; i < size; i++) {
            double d = (Double)this.values.get(i);
            if (Math.abs(d - domainValue) < epsilon)
                differences.put(d, i);
            else if (Math.abs(d - domainValue) < extreme_epsilon)
                rough_differences.put(d, i);
        }

        if (!differences.isEmpty())
            return differences.get(differences.keySet().stream().min(Double::compare).get());
        else if (!rough_differences.isEmpty())
            return rough_differences.get(rough_differences.keySet().stream().min(Double::compare).get());
        else
            return Integer.MIN_VALUE;
    }

    public void setClosestDomainValue(final double domainValue) {
        int closest = getClosestDoubleIn(domainValue);

        if (closest != Integer.MIN_VALUE)
            this.setValue(closest);
        else
            this.setValue(this.size / 2);
    }
}
