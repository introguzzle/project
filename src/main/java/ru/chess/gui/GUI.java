package ru.chess.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GUI {

    public static class Adapter {

        public static int VERTICAL_BOUND   = Board.VERTICAL_BOUND;
        public static int HORIZONTAL_BOUND = Board.HORIZONTAL_BOUND;

        public static final int MAX_DIMENSION = 640;

        public static Dimension getFittingCellDimension() {
            int size = MAX_DIMENSION / Math.max(VERTICAL_BOUND, HORIZONTAL_BOUND);

            return size > 80 ? new Dimension(80, 80) : new Dimension(size, size);
        }

        public static Font getFittingFont() {
            Font font = Cell.NOTATION_FONT;

            return new Font(font.getFontName(), font.getStyle(), getFittingCellDimension().height / (35 / 10));
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

    public static void setQuality(Graphics2D g2d, int quality) {

        if (quality == 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        if (quality == 1) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }

        if (quality == 2) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
    }

}