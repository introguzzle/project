package ru.grapher.core;

import ru.grapher.GUI;

import javax.swing.*;
import java.awt.*;

import static ru.grapher.GUI.__UNIVERSAL_BORDER;
import static ru.grapher.GUI.font;

public class DynamicButton extends JButton {

    private final Color hoverBackgroundColor;
    private final Color pressedBackgroundColor;

    public DynamicButton() {
        this(null);
    }

    public DynamicButton(String text) {
        this(text, 17);
    }

    public DynamicButton(String text,
                         int fontSize) {
        this(text, fontSize, new Color(184, 207, 228), new Color(160, 170, 255));
    }

    public DynamicButton(String text,
                         int fontSize,
                         Color hoverBackgroundColor,
                         Color pressedBackgroundColor) {
        super(text);
        super.setContentAreaFilled(false);

        this.setBackground(Color.WHITE);
        this.setFont(font(fontSize));
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

        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);

        if (getModel().isPressed() && getModel().isRollover()) {
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
