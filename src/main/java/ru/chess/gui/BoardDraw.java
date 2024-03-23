package ru.chess.gui;

import java.awt.*;

class BoardDraw {
    static void paintFontOutline(Graphics2D g2d,
                                 String     string,
                                 int        fx,
                                 int        fy,
                                 int        outlineWidth,
                                 Color      outlineColor) {
        Color oldColor = g2d.getColor();
        Font  oldFont  = g2d.getFont();

        g2d.setColor(outlineColor);

        g2d.drawString(string, fx - outlineWidth, fy - outlineWidth);
        g2d.drawString(string, fx - outlineWidth, fy + outlineWidth);
        g2d.drawString(string, fx + outlineWidth, fy - outlineWidth);
        g2d.drawString(string, fx + outlineWidth, fy + outlineWidth);

        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }

    static void paintFontShadow(Graphics2D g2d,
                                String     string,
                                int        fx,
                                int        fy,
                                int        shadowWidth,
                                Color      shadowColor) {
        Color old = g2d.getColor();

        g2d.setColor(shadowColor);
        g2d.drawString(string, fx + shadowWidth, fy);
        g2d.drawString(string, fx, fy + shadowWidth);
        g2d.drawString(string, fx - shadowWidth, fy);
        g2d.drawString(string, fx, fy - shadowWidth );

        g2d.setColor(old);
    }
}
