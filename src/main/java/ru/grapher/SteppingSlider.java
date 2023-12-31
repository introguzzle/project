package ru.grapher;

import javax.swing.*;
import java.util.*;

public final class SteppingSlider<T> extends JSlider {

    private ArrayList<T> values = new ArrayList<>();
    private int size;

    private final double epsilon = 1.0 / Math.pow(10.0, 2.0);
    private final double fnl = 1.0;

    public SteppingSlider() {
    }

    public SteppingSlider(final ArrayList<T> values, final Hashtable<Integer, JLabel> labels, final int defaultIndex) {
        super(0, values.size() - 1, defaultIndex);
        setLabelTable(labels);
        setPaintTicks(false);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(1);
        this.values = values;
        this.size = values.size();
    }

    public void init(final ArrayList<T> values, final int defaultIndex) {
        this.setMinimum(0);
        this.setMaximum(values.size() - 1);
        this.setValue(defaultIndex);
        this.values = values;
        this.size = values.size();
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
        Map<Double, Integer> differences = new HashMap<>();
        Map<Double, Integer> rough = new HashMap<>();

        for (int i = 0; i < size; i++) {
            double d = (Double)this.values.get(i);
            if (Math.abs(d - domainValue) < epsilon)
                differences.put(d, i);
            else if (Math.abs(d - domainValue) < fnl)
                rough.put(d, i);
        }

        if (!differences.isEmpty())
            return differences.get(differences.keySet().stream().min(Double::compare).get());
        else if (!rough.isEmpty())
            return rough.get(rough.keySet().stream().min(Double::compare).get());
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
