package ru.calculator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

class GUI {
    static final Color  GRAY = new Color(70, 70, 70);
    static final Color  INV = new Color(220, 220, 220);

    static final Font   FONT = ru.grapher.GUI.font(21);
    static final Border COMPOUND_BORDER = new CompoundBorder(
            BorderFactory.createLineBorder(INV, 1, false),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
    );

    static final Border BORDER = ru.grapher.GUI.__UNIVERSAL_BORDER;
}
