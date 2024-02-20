package ru.grapher;

import javax.swing.*;
import java.awt.*;

import static ru.grapher.GrapherGUI.__UNIVERSAL_BORDER;
import static ru.grapher.GrapherGUI.getDefaultFont;

public class DynamicButton extends JButton {

    private final Color hoverBackgroundColor;
    private final Color pressedBackgroundColor;

    public DynamicButton() {
        this(null);
    }

    public DynamicButton(String text) {
        this(text, 17);
    }

    public DynamicButton(String text, int fontSize) {
        this(text, fontSize, new Color(184, 207, 228), new Color(160, 170, 255));
    }

    public DynamicButton(String text, int fontSize, Color hoverBackgroundColor, Color pressedBackgroundColor) {
        super(text);
        super.setContentAreaFilled(false);

        this.setBackground(Color.WHITE);
        this.setFont(getDefaultFont(fontSize));
        this.setFocusable(false);
        this.setHorizontalTextPosition(JButton.CENTER);
        this.setVerticalTextPosition(JButton.CENTER);
        this.setBorder(__UNIVERSAL_BORDER);

        this.hoverBackgroundColor   = hoverBackgroundColor;
        this.pressedBackgroundColor = pressedBackgroundColor;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING   , RenderingHints.VALUE_RENDER_QUALITY);

        if (getModel().isPressed()) {
            g2d.setColor(pressedBackgroundColor);

        } else if (getModel().isRollover()) {

            g2d.setColor(hoverBackgroundColor);

        } else {
            g2d.setColor(getBackground());
        }

        g2d.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g2d);
    }

    @Override
    public void setContentAreaFilled(boolean b) {

    }
}
