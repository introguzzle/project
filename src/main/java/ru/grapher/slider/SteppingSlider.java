package ru.grapher.slider;

import ru.grapher.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public abstract class SteppingSlider<T> extends JSlider {

    protected List<T> domainValues = new ArrayList<>();
    protected int size;

    public SteppingSlider() {
        super();
    }

    public SteppingSlider(final List<T> domainValues,
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

        this.addMouseListener(new MouseHandler(this));
    }

    public SteppingSlider(final List<T> domainValues,
                          final Hashtable<Integer, JLabel> labels,
                          final int defaultIndex,
                          final boolean paintTicks) {
        this(domainValues, labels, defaultIndex);
        this.setPaintTicks(paintTicks);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g);
    }

    public void setConfiguration(final List<T> domainValues,
                                 final int current,
                                 final Hashtable<Integer, JLabel> labels) {
        this.setMinimum(0);
        this.setMaximum(domainValues.size() - 1);
        this.setValue(current);

        this.setLabelTable(labels);

        this.domainValues = domainValues;
        this.size = domainValues.size();
    }

    public void setThumbColor(Color thumbColor) {
        ((SteppingSliderUI) this.getUI()).thumbColor = thumbColor;
        this.repaint();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);

        try {
            this.setThumbColor(b ? Color.RED : Color.BLACK);
        } catch (Exception ignored) {

        }
    }

    public List<T> getDomainValues() {
        return this.domainValues;
    }

    public T getDomainValue() {
        return this.domainValues.get(this.getValue());
    }

    private static final class MouseHandler extends MouseAdapter {

        private final JSlider owner;
        private boolean hover;

        public MouseHandler(JSlider owner) {
            this.owner = owner;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (this.owner.isEnabled()) {
                this.hover = true;
                this.owner.setBackground(new Color(160, 170, 255));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (this.owner.isEnabled()) {
                this.hover = true;
                this.owner.setBackground(new Color(184, 207, 228));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (this.owner.isEnabled()) {
                this.hover = false;
                this.owner.setBackground(Color.WHITE);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (this.owner.isEnabled()) {
                if (!hover)
                    this.mouseExited(e);
                else
                    this.mouseEntered(e);
            }
        }
    }
}
