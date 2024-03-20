package ru.grapher.core;

import ru.grapher.GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TextField extends JTextField {

    public TextField(Border border,
                     Font font,
                     String initialText,
                     DocumentChangeListener listener) {
        super();

        this.setDoubleBuffered(true);
        this.setBorder(border);
        this.setFont(font);
        this.setText(initialText);
        if (listener != null)
            this.getDocument().addDocumentListener(listener);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
        super.paint(g2d);
    }

    public void clear() {
        this.setText("");
    }

    public void setCaretPositionAtEnd() {
        try {
            this.setCaretPosition(this.getText().length());
        } catch (IllegalArgumentException ignored) {

        }
    }
}
