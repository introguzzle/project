package com.mathp;

import javax.swing.*;
import java.util.Hashtable;

public class SteppingSlider extends JSlider {

    private Integer[] values = {};

    public SteppingSlider() {
    }

    public SteppingSlider(Integer[] _values, Hashtable<Integer, JLabel> labels, int defaultIndex) {
        super(0, _values.length - 1, defaultIndex);
        setLabelTable(labels);
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(1);
        this.values = _values;
    }

    public void init(Integer[] _values, int defaultIndex) {
        this.setMinimum(0);
        this.setMaximum(_values.length - 1);
        this.setValue(defaultIndex);
        this.values = _values;
    }

    public Integer getDomainValue() {
        return this.values[this.getValue()];
    }

    public void setDomainValue(int domainValue) {
        for (int i = 0; i < this.values.length; i++) {
            if (domainValue == this.values[i])
                this.setValue(i);
        }
    }

}
