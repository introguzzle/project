package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SteppingSlider<T> extends JSlider {

    protected ArrayList<T> domainValues = new ArrayList<>();
    protected int size;

    public SteppingSlider() {
        super();
    }

    public SteppingSlider(final ArrayList<T> domainValues,
                          final Hashtable<Integer, JLabel> labels,
                          final int defaultIndex) {
        super(0, domainValues.size() - 1, defaultIndex);
        this.setUI(new SteppingSliderUI(this, Color.BLACK));
        this.setLabelTable(labels);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setSnapToTicks(true);
        this.setMajorTickSpacing(1);
        this.setFocusable(false);

        this.domainValues = domainValues;
        this.size = domainValues.size();
    }

    public SteppingSlider(final ArrayList<T> domainValues,
                          final Hashtable<Integer, JLabel> labels,
                          final int defaultIndex,
                          final boolean paintTicks) {
        this(domainValues, labels, defaultIndex);
        this.setPaintTicks(paintTicks);
    }

    public void setConfiguration(final ArrayList<T> domainValues,
                                 final int current,
                                 final Hashtable<Integer, JLabel> labels) {
        this.setMinimum(0);
        this.setMaximum(domainValues.size() - 1);
        this.setValue(current);

        this.setLabelTable(labels);

        this.domainValues = domainValues;
        this.size = domainValues.size();
    }

    // must be same type,
    // but maybe I could write this using wildcard as
    // public void setConfiguration(SteppingSlider<?> other) {
    //
    //

    public void setConfiguration(final SteppingSlider<T> other,
                                 final int defaultIndex,
                                 final Hashtable<Integer, JLabel> newLabels) {
        this.setConfiguration(other.domainValues, defaultIndex, newLabels);
    }

    public void setThumbColor(Color thumbColor) {
        this.setUI(new SteppingSliderUI(this, thumbColor));
    }

    public ArrayList<T> getDomainValues() {
        return this.domainValues;
    }

    public T getDomainValue() {
        return this.domainValues.get(this.getValue());
    }

    public void setDomainValue(final T domainValue) {
        for (int i = 0; i < size; i++) {
            if (domainValue == this.domainValues.get(i))
                this.setValue(i);
        }
    }
}
