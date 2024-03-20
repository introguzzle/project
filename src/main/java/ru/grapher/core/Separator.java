package ru.grapher.core;

import javax.swing.*;
import java.awt.*;

public class Separator extends JSeparator {

    public Separator(Color b, Color f) {
        super();

        this.setDoubleBuffered(true);
        this.setBackground(b);
        this.setForeground(f);
    }
}
