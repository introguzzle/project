package ru.chess;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ChessGUI {

    public static class Cell {

        public static final Color  WHITE_COLOR = new Color(240, 220, 220);
        public static final Color  WHITE_MOVE_COLOR = WHITE_COLOR.darker();
        public static final Color  WHITE_SELECTED_COLOR = WHITE_COLOR;

        public static final Color  BLACK_COLOR = new Color(160, 110, 0);
        public static final Color  BLACK_MOVE_COLOR = BLACK_COLOR.darker();
        public static final Color  BLACK_SELECTED_COLOR = BLACK_COLOR;

        public static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.BLACK, 4);

        public static final Color  AVAILABLE_MOVE_COLOR  = new Color(90, 80, 80);
        public static final int    AVAILABLE_MOVE_RADIUS = 20;

        public static final Color  NOTED = Color.RED.darker();

        public static final Font   NOTATION_FONT = new Font("Arial", Font.PLAIN, 22);

    }

    public static void setQuality(Graphics2D g2d, int quality) {

//        g2d.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_SIZE_FIT);
//        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,     RenderingHints.VALUE_STROKE_NORMALIZE);

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
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
    }

}