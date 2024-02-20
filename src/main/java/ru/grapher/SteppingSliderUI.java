package ru.grapher;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.geom.*;

public class SteppingSliderUI extends BasicSliderUI {

    private final Color thumbColor;

    private final BasicStroke stroke = new BasicStroke(
            1f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND,
            0f,
            new float[]{1f, 2f},
            0f
    );

    public SteppingSliderUI(JSlider slider, Color thumbColor) {
        super(slider);
        this.thumbColor = thumbColor;
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
        Graphics2D g2d = (Graphics2D) graphics;

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        super.paint(graphics, component);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(12, 16);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING   , RenderingHints.VALUE_RENDER_QUALITY);

        Stroke old = g2d.getStroke();

        g2d.setStroke(stroke);
        g2d.setPaint(Color.BLACK);

        if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
            g2d.drawLine(
                    trackRect.x,
                    trackRect.y + trackRect.height / 2,
                    trackRect.x + trackRect.width,
                    trackRect.y + trackRect.height / 2);

        } else {
            g2d.drawLine(
                    trackRect.x + trackRect.width / 2,
                    trackRect.y,
                    trackRect.x + trackRect.width / 2,
                    trackRect.y + trackRect.height
            );
        }

        g2d.setStroke(old);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING   , RenderingHints.VALUE_RENDER_QUALITY);

        Shape shape = new Rectangle(thumbRect.x + 2,
                thumbRect.y + 3,
                9,
                9
        );

        if (!this.slider.getValueIsAdjusting()) {
            g2d.setStroke(new BasicStroke(1f));
            g2d.setPaint(thumbColor);

            g2d.fill(shape);
            g2d.draw(shape);

        } else {
            g2d.setStroke(new BasicStroke(1f));
            g2d.setPaint(Color.BLACK);

            g2d.fill(shape);
            g2d.draw(shape);
        }
    }

}

