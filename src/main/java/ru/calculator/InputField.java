package ru.calculator;

import ru.grapher.core.TextField;

import java.awt.*;

class InputField extends TextField {
    InputField() {
        super(GUI.BORDER, GUI.FONT, "", null);

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }
}
