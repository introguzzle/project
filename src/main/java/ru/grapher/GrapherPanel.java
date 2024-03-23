package ru.grapher;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;

public class GrapherPanel extends ChartPanel {

    private static final int  TABLE_WIDTH  = 120;
    private static final int  TABLE_HEIGHT = 120;

    private static final Font FONT = GUI.font(20);

    private final Shape TABLE = new Rectangle(1, 1, TABLE_WIDTH, TABLE_HEIGHT);

    private final StatelessGrapher grapher;
    GrapherPanel(JFreeChart chart, StatelessGrapher grapher) {
        super(chart);

        this.grapher = grapher;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g2d);

        drawTable(g2d);
        drawCoefficients(g2d);
    }

    private void silentDraw(Graphics g2d, Runnable action) {
        Color oldColor = g2d.getColor();
        Font  oldFont  = g2d.getFont();

        action.run();

        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }

    private void drawTable(Graphics2D g2d) {
        this.silentDraw(g2d, () -> {
            g2d.setColor(Color.WHITE);
            g2d.fill(TABLE);

            g2d.setColor(Color.BLACK);
            g2d.draw(TABLE);
        });
    }

    private void drawCoefficients(Graphics2D g2d) {
        this.silentDraw(g2d, () -> {
            g2d.setFont(GrapherPanel.FONT);
            g2d.setColor(Color.BLACK);

            int x = 5;
            int y = 20;

            for (var entry : grapher.coefficientMap.entrySet()) {
                String coefficient = entry.getKey() + ": " + entry.getValue();
                g2d.drawString(coefficient, x, y);
                y += g2d.getFontMetrics().getHeight();
            }
        });
    }
}
