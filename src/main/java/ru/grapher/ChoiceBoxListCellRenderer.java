package ru.grapher;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

public class ChoiceBoxListCellRenderer extends DefaultListCellRenderer {

    private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        Component c = renderer.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);

        if (c instanceof JLabel) {
            if (isSelected) {
                c.setBackground(Color.blue);
            } else {
                c.setBackground(Color.red);
            }
        } else {
            c.setBackground(Color.red);
            c = super.getListCellRendererComponent(list, value, index, isSelected,
                    cellHasFocus);
        }
        return c;
    }
}
