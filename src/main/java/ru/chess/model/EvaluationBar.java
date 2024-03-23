package ru.chess.model;

import ru.chess.gui.GUI;
import ru.utils.ColorUtilities;

import javax.swing.*;
import java.awt.*;

public class EvaluationBar extends JPanel {

    private static final double MAX_VALUE = 60;
    private static final double MIN_VALUE = -MAX_VALUE;

    private static final Color  WHITE_COLOR = GUI.Cell.WHITE_COLOR;
    private static final Color  BLACK_COLOR = GUI.Cell.BLACK_COLOR;

    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 1);

    private double value;

    public static final int WIDTH  = 60;
    public static final int HEIGHT = 640;

    private Timer timer;

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
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g2d);

        int divisor = (int) (HEIGHT * (1.0 - normalize()));

        Rectangle WHITE_RECTANGLE = new Rectangle(0, divisor, WIDTH, HEIGHT);
        Rectangle BLACK_RECTANGLE = new Rectangle(0, 0, WIDTH, divisor);

        g2d.setColor(WHITE_COLOR);
        g2d.draw(WHITE_RECTANGLE);
        g2d.fill(WHITE_RECTANGLE);

        g2d.setColor(BLACK_COLOR);
        g2d.draw(BLACK_RECTANGLE);
        g2d.fill(BLACK_RECTANGLE);

        g2d.setColor(Color.BLACK);

        this.paintBorder(g2d);
        this.paintValue(g2d);
    }

    private double normalize() {
        return Math.min(1, Math.max((this.value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE), 0));
    }

    private void paintValue(Graphics2D g2d) {
        String string = extend(String.valueOf(this.value), 5);

        g2d.setFont(GUI.Adapter.getFittingFont(this, g2d, VALUE_FONT, string, 80));

        int stringWidth  = g2d.getFontMetrics().stringWidth(string);
        int stringHeight = g2d.getFontMetrics().getHeight();

        g2d.setColor(ColorUtilities.constrastingOf(g2d.getBackground()));
        g2d.drawString(string, (WIDTH - stringWidth) / 2, (HEIGHT + stringHeight) / 2);
    }

    private static String extend(String string, int length) {
        return string.length() > length ? string.substring(0, length)
                : string + "0".repeat(length - string.length() + (string.charAt(0) == '-' ? 1 : 0));
    }

    public void setValue(double value) {
        animate(value, () -> this.value = value);
    }

    private void animate(double newValue, Runnable callback) {
        final double steps = 150;
        final double delta = (newValue - this.value) / steps;

        SwingWorker<Void, Void> animator = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }

                timer = new Timer(0, (e) -> {
                    value += delta;
                    repaint();

                    if (Math.abs(value - newValue) <= Math.abs(delta * 2)) {
                        ((Timer) e.getSource()).stop();
                        callback.run();
                    }
                });

                timer.start();

                return null;
            }
        };

        animator.execute();
    }
}
