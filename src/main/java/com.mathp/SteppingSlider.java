package com.mathp;

import javax.swing.*;
import java.util.Hashtable;

public final class SteppingSlider extends JSlider {

    private Integer[] values = {};

    public SteppingSlider() {
    }

    public SteppingSlider(final Integer[] _values, final Hashtable<Integer, JLabel> labels, final int defaultIndex) {
        super(0, _values.length - 1, defaultIndex);
        setLabelTable(labels);
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(1);
        this.values = _values;
    }

    public void init(final Integer[] _values, final int defaultIndex) {
        this.setMinimum(0);
        this.setMaximum(_values.length - 1);
        this.setValue(defaultIndex);
        this.values = _values;
    }

    public Integer getDomainValue() {
        return this.values[this.getValue()];
    }

    public void setDomainValue(final int domainValue) {
        for (int i = 0; i < this.values.length; i++) {
            if (domainValue == this.values[i])
                this.setValue(i);
        }
    }

}
