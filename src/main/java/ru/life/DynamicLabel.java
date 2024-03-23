package ru.life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Practically a JButton. Can't be used on board
 */
public class DynamicLabel extends JLabel {

    private static final Dimension DIMENSION = GUI.LABEL_DIMENSION;

    private final Color  defaultColor;
    private final Color  movedColor;
    private final String movedText;

    private boolean paintMovedText;

    private final MouseHandler mouseHandler;

    /**
     *
     * @param defaultColor Default color of label
     * @param movedColor Color of cell when entered this label
     * @param icon Icon of this label
     * @param pressedAction What to perform when pressed
     */
    public DynamicLabel(Color defaultColor,
                        Color movedColor,
                        Icon icon,
                        String movedText,
                        Runnable pressedAction) {
        this.defaultColor = defaultColor;
        this.movedColor   = movedColor;

        this.movedText    = movedText;

        this.setBackground(defaultColor);

        this.setOpaque(true);
        this.setPreferredSize(DIMENSION);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setIcon(icon);

        mouseHandler = new MouseHandler(this, pressedAction);

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g2d);

        if (this.paintMovedText) {
            Font oldFont = g2d.getFont();
            Color oldColor = g2d.getColor();

            int fontSize = 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
//            g2d.setColor(ColorUtilities.constrastingOf(this.getBackground()));
            g2d.setColor(Color.WHITE);

            g2d.drawString(
                    this.movedText,
                    fontSize / 4,
                    this.getHeight() - fontSize / 4
            );

            g2d.setFont(oldFont);
            g2d.setColor(oldColor);
        }
    }

    public void on() {
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    public void off() {
        this.removeMouseListener(mouseHandler);
        this.removeMouseMotionListener(mouseHandler);
    }

    public static class MouseHandler extends MouseAdapter {

        private final Runnable     pressedAction;
        private final DynamicLabel label;

        private boolean onLabel;

        public MouseHandler(DynamicLabel label,
                            Runnable pressedAction) {
            this.label = label;
            this.pressedAction = pressedAction;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            label.setBackground(label.movedColor.darker());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (onLabel) {
                pressedAction.run();

                label.setBackground(label.movedColor);
                label.paintMovedText = true;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            onLabel = true;
            label.setBackground(label.movedColor);
            label.paintMovedText = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            onLabel = false;
            label.setBackground(label.defaultColor);
            label.paintMovedText = false;
        }
    }
}

