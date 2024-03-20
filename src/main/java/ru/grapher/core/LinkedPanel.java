package ru.grapher.core;

import ru.grapher.GUI;

import javax.swing.*;
import java.awt.*;

public abstract class LinkedPanel extends JPanel {

    private boolean done = false;

    public LinkedPanel() {
        super();
    }

    public final void setDone(boolean done) {
        this.done = done;
    }
    public final boolean isDone() {
        return this.done;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g2d);
    }
}
