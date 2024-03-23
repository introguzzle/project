package ru.chess.label;

import ru.chess.gui.Board;
import ru.chess.gui.GUI;
import ru.utils.ColorUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Practically a JButton. Can't be used on board
 */
public class DynamicLabel extends JLabel {

    private final Color  defaultColor;
    private final Color  movedColor;
    private final String movedText;

    private boolean paintMovedText;

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
                        Action pressedAction) {
        this.defaultColor = defaultColor;
        this.movedColor   = movedColor;

        this.movedText    = movedText;

        this.setOpaque(true);
        this.setPreferredSize(Board.DIMENSION_CELL);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setIcon(icon);

        MouseHandler mouseHandler = new MouseHandler(this, pressedAction);

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
            g2d.setColor(ColorUtilities.constrastingOf(this.getBackground()));

            g2d.drawString(
                    this.movedText,
                    fontSize / 4,
                    this.getHeight() - fontSize / 4
            );

            g2d.setFont(oldFont);
            g2d.setColor(oldColor);
        }
    }

    public static class MouseHandler extends MouseAdapter {

        private final Action         pressedAction;
        private final DynamicLabel   label;
        private       boolean        onLabel;

        public MouseHandler(DynamicLabel label, Action pressedAction) {
            this.label = label;
            this.pressedAction = pressedAction;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.label.setBackground(this.label.movedColor.darker());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (this.onLabel) {
                this.pressedAction.accept(e);

                this.label.setBackground(this.label.movedColor);
                this.label.paintMovedText = true;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.onLabel = true;
            this.label.setBackground(this.label.movedColor);
            this.label.paintMovedText = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            this.onLabel = false;
            this.label.setBackground(this.label.defaultColor);
            this.label.paintMovedText = false;
        }
    }
}
