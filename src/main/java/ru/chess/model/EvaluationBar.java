package ru.chess.model;

import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;

public class EvaluationBar extends JPanel {

    private static final double MAX_VALUE = 60;
    private static final double MIN_VALUE = -MAX_VALUE;

    private double value;

    public static final int WIDTH  = 60;
    public static final int HEIGHT = 640;

    public static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 1);

    public EvaluationBar(double initialValue) {
        super();

        this.value = initialValue;

        init();
    }

    private void init() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GUI.setQuality(g2d, 2);
        super.paint(g);

        int divisor = (int) (HEIGHT * (1.0 - normalize()));

        Rectangle white = new Rectangle(0, divisor, WIDTH, HEIGHT);
        Rectangle black = new Rectangle(0, 0, WIDTH, divisor);

        g2d.setColor(GUI.Cell.WHITE_COLOR);
        g2d.draw(white);
        g2d.fill(white);

        g2d.setColor(GUI.Cell.BLACK_COLOR);
        g2d.draw(black);
        g2d.fill(black);

        g2d.setColor(Color.BLACK);

        this.paintBorder(g2d);
        this.paintValue(g2d);
    }

    private double normalize() {
        return Math.min(1, Math.max((this.value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE), 0));
    }

    private void paintValue(Graphics2D g2d) {
        String string = String.valueOf(this.value);

        g2d.setFont(GUI.Adapter.getFittingFont(this, g2d, VALUE_FONT, string, 80));

        g2d.setColor(Color.BLACK);

        int stringWidth  = g2d.getFontMetrics().stringWidth(string);
        int stringHeight = g2d.getFontMetrics().getHeight();

        g2d.drawString(string, (WIDTH - stringWidth) / 2, (HEIGHT + stringHeight) / 2);
    }

    public void setValue(double value) {
        this.value = value;
        this.repaint();
    }
}
