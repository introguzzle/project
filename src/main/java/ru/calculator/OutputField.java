package ru.calculator;

import ru.grapher.core.TextField;

class OutputField extends TextField {
    static final double DIGIT_COUNT = 2;

    OutputField() {
        super(GUI.COMPOUND_BORDER, GUI.FONT, "", null);

        setEditable(false);

        setBackground(GUI.GRAY);
        setForeground(GUI.INV);
    }

    void setValue(String value) {
        try {
            double d = Double.parseDouble(value);
            double m = Math.pow(10.0, DIGIT_COUNT);

            this.setText(String.valueOf(Math.round(d * m) / m));

        } catch (NumberFormatException | NullPointerException ignored) {

        }
    }
}
