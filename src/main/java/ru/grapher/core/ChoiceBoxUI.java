package ru.grapher.core;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ChoiceBoxUI extends BasicComboBoxUI {

    protected JButton arrowButton;

    static {
        UIManager.put("ComboBox.buttonBackground", Color.BLACK);
        UIManager.put("ComboBox.buttonShadow"    , Color.WHITE);
        UIManager.put("ComboBox.buttonDarkShadow", Color.WHITE);
        UIManager.put("ComboBox.buttonHighlight",  Color.WHITE);
    }

    @Override
    protected JButton createArrowButton() {
        arrowButton = new BasicArrowButton(BasicArrowButton.SOUTH,
                UIManager.getColor("ComboBox.buttonBackground"),
                UIManager.getColor("ComboBox.buttonShadow"),
                UIManager.getColor("ComboBox.buttonDarkShadow"),
                UIManager.getColor("ComboBox.buttonHighlight"));

        arrowButton.setName("ComboBox.arrowButton");

        arrowButton.setDoubleBuffered(true);
        arrowButton.setFocusable(false);

        return arrowButton;
    }

    @Override
    public void paintCurrentValue(Graphics g,
                                  Rectangle bounds,
                                  boolean hasFocus) {
        ListCellRenderer<Object> renderer = comboBox.getRenderer();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox,
                    comboBox.getSelectedItem(),
                    -1,
                    true,
                    false
            );
        } else {
            c = renderer.getListCellRendererComponent(listBox,
                    comboBox.getSelectedItem(),
                    -1,
                    false,
                    false
            );

            c.setBackground(Color.WHITE);
        }

        c.setFont(comboBox.getFont());

        if (hasFocus && !isPopupVisible(comboBox)) {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        } else {

            if (comboBox.isEnabled()) {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            } else {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);
            }
        }

        boolean shouldValidate = c instanceof JPanel;

        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;

        if (padding != null) {
            x = bounds.x + padding.left;
            y = bounds.y + padding.top;
            w = bounds.width - (padding.left + padding.right);
            h = bounds.height - (padding.top + padding.bottom);
        }

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);

        arrowButton.setForeground(Color.BLACK);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g,
                                            Rectangle bounds,
                                            boolean hasFocus) {
        Color t = g.getColor();

        arrowButton.setForeground(Color.BLACK);

        if (comboBox.isEnabled())
            g.setColor(Color.WHITE);
        else
            g.setColor(Color.WHITE);

        g.fillRect(bounds.x,
                bounds.y,
                bounds.width,
                bounds.height);
        g.setColor(t);

        arrowButton.setForeground(Color.BLACK);
    }
}
