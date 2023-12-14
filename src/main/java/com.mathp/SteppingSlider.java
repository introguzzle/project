package com.mathp;

import javax.swing.*;
import java.util.Hashtable;

public class SteppingSlider extends JSlider {

    private Integer[] values = {};

    public SteppingSlider() {
    }

    public SteppingSlider(Integer[] _values, Hashtable<Integer, JLabel> labels, int indexOfHundred) {
        super(0, _values.length - 1, indexOfHundred);
        setLabelTable(labels);
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(1);
        this.values = _values;
    }

    public Integer getDomainValue() {
        return this.values[this.getValue()];
    }

}
