package ru.chess.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GUI {

    public static class Adapter {

        public static int VERTICAL_BOUND   = Board.VERTICAL_BOUND;
        public static int HORIZONTAL_BOUND = Board.HORIZONTAL_BOUND;

        public static final int MAX_HEIGHT = 640;

        public static Dimension getFittingCellDimension() {
            int size = MAX_HEIGHT / Math.max(VERTICAL_BOUND, HORIZONTAL_BOUND);

            return size > 80 ? new Dimension(80, 80) : new Dimension(size, size);
        }

        public static Font getFittingFont(JComponent component,
                                          Graphics2D g2d,
                                          Font font,
                                          String sample,
                                          int usagePercent) {
            Font oldFont = g2d.getFont();

            int w = component.getWidth();
            int h = component.getHeight();

            int size = 1;

            int wS = (int) (w * ((double) usagePercent / 100));
            int hS = (int) (h * ((double) usagePercent / 100));

            while (g2d.getFontMetrics().getHeight() < hS
                    && g2d.getFontMetrics().stringWidth(sample) < wS) {
                g2d.setFont(new Font(font.getFontName(), font.getStyle(), size));

                size++;
            }

            g2d.setFont(oldFont);

            return new Font(font.getFontName(), font.getStyle(), size);
        }
    }

    public static class Cell {

        public static final Color  WHITE_COLOR = new Color(240, 220, 220);
        public static final Color  WHITE_SELECTED_COLOR = WHITE_COLOR;

        public static final Color  BLACK_COLOR = new Color(160, 110, 0);
        public static final Color  BLACK_SELECTED_COLOR = BLACK_COLOR;

        public static final Color  SELECTED_BORDER_COLOR = Color.BLACK;

        public static final Color  AVAILABLE_MOVE_COLOR  = new Color(90, 80, 80);

        public static final Color  CHECKMATE_NOTED_COLOR = Color.RED.darker();
        public static final Color  STALEMATE_NOTED_COLOR = Color.BLUE.darker();

        public static final Font   NOTATION_FONT = new Font("Arial", Font.PLAIN, 22);

    }

    public static Map<?, ?> Q_RENDERING_HINTS = Map.of(
            RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY,
            RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BICUBIC
    );
}