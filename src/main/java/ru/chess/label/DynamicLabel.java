package ru.chess.label;

import ru.chess.gui.Board;
import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Practically a JButton. Can't be used on board
 */
public class DynamicLabel extends JLabel {

    private final Color defaultColor;
    private final Color movedColor;

    /**
     *
     * @param defaultColor Default color of label
     * @param movedColor Color of cell when entered this label
     * @param icon Icon of this label
     * @param pressedAction What to perform when pressed
     */
    public DynamicLabel(Color defaultColor, Color movedColor, Icon icon, Action pressedAction) {
        this.defaultColor = defaultColor;
        this.movedColor   = movedColor;

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
        GUI.setQuality(g2d, 2);
        super.paint(g2d);
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
            this.mouseEntered(e);

            if (this.onLabel)
                this.pressedAction.actionPerformed(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.onLabel = true;
            this.label.setBackground(this.label.movedColor);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            this.onLabel = false;
            this.label.setBackground(this.label.defaultColor);
        }
    }
}
